package imag.quizz.server.network;

import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.InitMessage;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.Pair;
import imag.quizz.common.tool.SockUri;
import imag.quizz.server.ServerController;
import imag.quizz.server.game.Server;

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

    public ServerConnectionManager(final ServerController controller, final long ownId) {
        super(controller, false, controller.getConfig().getServers().get(controller.getOwnId()).getServerPort(), ownId);
        this.controller = controller;
        this.executor = Executors.newCachedThreadPool();

        // Attempt to join other servers
        this.initialize();
    }

    /**
     * Connects to other servers then select the current leader.
     */
    private void initialize() {
        final String uri = this.connectLeader();

        if (uri == null) {
            this.controller.setLeader(true);
            this.controller.setCurrentLeaderId(this.controller.getOwnId());
            Log.info("Server started alone.");
        } else {
            if (this.controller.getCurrentLeaderId() > this.getOwnId()) {
                this.controller.setLeader(false);
                Log.info("Server started as future new leader, waiting for INIT from current leader");
            } else {
                this.controller.setLeader(false);
                this.controller.setCurrentLeaderUri(uri);
                Log.info("Server started as lambda server, waiting for INIT from current leader");
            }
        }
    }

    /**
     * Attempts to find an existing leader.
     *
     * @return the uri of the connection to leader or null if there's none
     */
    private String connectLeader() {
        final List<Future<Pair<Long, String>>> futures = new LinkedList<>();
        for (final Entry<Long, ServerInfo> entry : this.controller.getConfig().getServers().entrySet()) {
            final long id = entry.getKey();
            if (id == this.getOwnId()) {
                // Ignore
                continue;
            }
            final ServerInfo info = entry.getValue();
            futures.add(this.executor.submit(new Callable<Pair<Long, String>>() {
                @Override
                public Pair<Long, String> call() throws Exception {
                    final Socket s = new Socket();
                    try {
                        s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                        try {
                            final String uri = SockUri.from(s);
                            ServerConnectionManager.this.newConnection(new Server(id, uri), s);
                            ServerConnectionManager.this.controller.getPingPongTask().addUri(uri);
                            return new Pair<>(id, uri);
                        } catch (final IOException e) {
                            Log.error("Failed to create SocketHandler for server " + id, e);
                        }
                    } catch (final IOException e) {
                        Log.debug("Failed to connect to server " + id + ", ignoring");
                        Log.trace("Error was:", e);
                    }
                    if (!ServerConnectionManager.this.controller.getServers().containsKey(id)) {
                        ServerConnectionManager.this.controller.getServers().put(id, new Server(id, null));
                    }
                    return null;
                }
            }));
        }
        String uri = null;
        final Iterator<Future<Pair<Long, String>>> it = futures.iterator();
        while (it.hasNext()) {
            final Future<Pair<Long, String>> future = it.next();
            try {
                final Pair<Long, String> res = future.get();
                if (res != null) {
                    final long id = res.getA();
                    uri = res.getB();
                    final Server server = (Server) this.connectedPeers.get(uri);
                    this.controller.getServers().put(server.getId(), server);
                    this.controller.setCurrentLeaderId(id);
                    this.controller.getServers().get(id).setLeader(true);
                    this.executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            while (it.hasNext()) {
                                try {
                                    it.next().get();
                                } catch (final Exception ignored) {
                                }
                            }
                        }
                    });
                    break;
                }
            } catch (final InterruptedException | ExecutionException e) {
                Log.fatal("Fatal error while connecting to server", e);
                System.exit(2549);
            }
        }

        this.executor.shutdown();
        this.executor = null;

        return uri;
    }

    @Override
    public void newIncomingConnection(final Socket socket) throws IOException {
        final String uri = SockUri.from(socket);
        super.newIncomingConnection(socket);
        if (this.controller.isLeader()) {
            final SocketHandler socketHandler = this.connections.get(uri);
            socketHandler.write(new InitMessage(this.controller.getOwnId(), this.controller.buildInitData()).toString());
        }
        ServerConnectionManager.this.controller.getPingPongTask().addUri(uri);
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
            for (final String uri : this.connectedPeers.keySet()) {
                this.connections.get(uri).write(message.toString());
            }
        } else {
            this.connections.get(this.controller.getCurrentLeaderUri()).write(message.toString());
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
    public void forgetConnection(final String uri) {
        super.forgetConnection(uri);
    }
}
