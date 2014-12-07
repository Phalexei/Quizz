package imag.quizz.server.network;

import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.InitMessage;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.Pair;
import imag.quizz.server.ServerController;
import imag.quizz.server.game.Server;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * Handles connections
 */
public class ServerConnectionManager extends ConnectionManager {

    private final ServerController controller;
    private       ExecutorService  executor;

    private boolean isConnectedToLambdas;

    public ServerConnectionManager(final ServerController controller, final long ownId) {
        super(controller, false, controller.getConfig().getServers().get(controller.getOwnId()).getServerPort(), ownId);
        this.controller = controller;
        this.executor = Executors.newCachedThreadPool();
        this.isConnectedToLambdas = false;

        // Attempt to join other servers
        this.initialize();
    }

    /**
     * Connects to other servers then select the current leader.
     */
    private void initialize() {
        final Integer port = this.connectLeader();

        if (port == null) {
            this.controller.setLeader(true);
            this.controller.setCurrentLeaderId(this.controller.getOwnId());
            Log.info("Server started alone.");
        } else {
            if (this.controller.getCurrentLeaderId() > this.getOwnId()) {
                this.controller.setLeader(false);
                Log.info("Server started as future new leader, waiting for INIT from current leader");
            } else {
                this.controller.setLeader(false);
                this.controller.setCurrentLeaderLocalPort(port);
                Log.info("Server started as lambda server, waiting for INIT from current leader");
            }
        }
    }

    /**
     * Attempts to find an existing leader.
     *
     * @return the local port of the connection to leader or null if there's
     * none
     */
    private Integer connectLeader() {
        final List<Future<Pair<Long, Integer>>> futures = new LinkedList<>();
        for (final Entry<Long, ServerInfo> entry : this.controller.getConfig().getServers().entrySet()) {
            final long id = entry.getKey();
            if (id == this.getOwnId()) {
                // Ignore
                continue;
            }
            final ServerInfo info = entry.getValue();
            futures.add(this.executor.submit(new Callable<Pair<Long, Integer>>() {
                @Override
                public Pair<Long, Integer> call() throws Exception {
                    final Socket s = new Socket();
                    try {
                        s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                        try {
                            ServerConnectionManager.this.newConnection(new Server(id, s.getLocalPort()), s);
                            return new Pair<>(id, s.getLocalPort());
                        } catch (final IOException e) {
                            Log.error("Failed to create SocketHandler for server " + id, e);
                        }
                    } catch (final IOException e) {
                        Log.debug("Failed to connect to server " + id + ", ignoring");
                        Log.trace("Error was:", e);
                    }
                    return null;
                }
            }));
        }
        final Iterator<Future<Pair<Long, Integer>>> it = futures.iterator();
        while (it.hasNext()) {
            final Future<Pair<Long, Integer>> future = it.next();
            try {
                final Pair<Long, Integer> res = future.get();
                if (res != null) {
                    this.controller.setCurrentLeaderId(res.getA());
                    while (it.hasNext()) {
                        it.next().get();
                    }
                    return res.getB();
                }
            } catch (final InterruptedException | ExecutionException e) {
                Log.fatal("Fatal error while connecting to server", e);
                System.exit(2549);
            }
        }

        return null;
    }

    /**
     * Attempts connections to all servers listed in the configuration file
     * except current leader and registers online servers.
     *
     * @return the amount of successful connections
     */
    public int connectServers() {
        Validate.isTrue(!this.isConnectedToLambdas, "Illegal State: already connected to servers once");
        final long oldLeaderId = this.controller.getCurrentLeaderId();
        if (this.controller.getCurrentLeaderId() > this.getOwnId()) {
            this.controller.setLeader(true);
            this.controller.setCurrentLeaderId(this.getOwnId());
            this.controller.setCurrentLeaderLocalPort(null);
            Log.info("We are now Leader");
        }
        final List<Future<Boolean>> futures = new LinkedList<>();
        for (final Entry<Long, ServerInfo> entry : this.controller.getConfig().getServers().entrySet()) {
            final long id = entry.getKey();
            if (id == this.getOwnId() || id == oldLeaderId) {
                // Ignore
                continue;
            }
            final ServerInfo info = entry.getValue();
            futures.add(this.executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final Socket s = new Socket();
                    boolean success = false;
                    try {
                        s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                        try {
                            ServerConnectionManager.this.newConnection(new Server(id, s.getLocalPort()), s);
                            success = true;
                        } catch (final IOException e) {
                            Log.error("Failed to create SocketHandler for server " + info.getId(), e);
                        }
                    } catch (final IOException e) {
                        Log.info("Failed to connect to server " + info.getId() + ", ignoring");
                        Log.trace("Error was:", e);
                    }
                    return success;
                }
            }));
        }
        int successCount = 0;
        for (final Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                }
            } catch (final InterruptedException | ExecutionException e) {
                Log.fatal("Fatal error while connecting to server", e);
                System.exit(784);
            }
        }
        this.isConnectedToLambdas = true;

        this.executor.shutdown();
        this.executor = null;

        return successCount;
    }

    @Override
    public void newIncomingConnection(final Socket socket) throws IOException {
        super.newIncomingConnection(socket);
        if (this.controller.isLeader()) {
            final SocketHandler socketHandler = this.connections.get(socket.getLocalPort());
            socketHandler.write(new InitMessage(this.controller.getOwnId(), this.controller.buildInitData()).toString());
        }
    }

    /**
     * Broadcasts a Message to every servers by either sending it to the
     * current leader if this server isn't the leader, or by sending it
     * to all online servers if it is.
     *
     * @param message the message
     */
    public void leaderBroadcast(final Message message) {
        if (this.controller.isLeader()) {
            for (final int port : this.connectedPeers.keySet()) {
                this.connections.get(port).write(message.toString());
            }
        } else {
            this.connections.get(this.controller.getCurrentLeaderLocalPort()).write(message.toString() + '\n');
        }
    }

    /**
     * Directly sends a message to every servers.
     *
     * @param message the message
     */
    public void broadcast(final Message message) {
        for (final SocketHandler handler : this.connections.values()) {
            handler.write(message.toString());
        }
    }

    @Override
    public void forgetConnection(int port) {
        super.forgetConnection(port);
        //TODO : devenir leader ? plus aucun server ? autre ?
    }
}
