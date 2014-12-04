package imag.quizz.server.network;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.Peer;
import imag.quizz.server.game.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Map.Entry;

/**
 * Handles connections
 */
public class ServerConnectionManager extends ConnectionManager {

    private final int    ownId;
    private final Config config;

    private boolean isLeader;
    private Integer currentLeaderLocalPort;

    public ServerConnectionManager(final MessageHandler messageHandler, final int ownId, final Config config) {
        super(messageHandler, false, config.getServers().get(ownId).getServerPort());
        this.ownId = ownId;
        this.config = config;

        // Attempt to join other servers
        this.initialize();
    }

    /**
     * Connects to other servers then select the current leader.
     */
    private void initialize() {
        final int serversOnline = this.connectServers();

        if (serversOnline == 0) {
            this.isLeader = true;
            this.currentLeaderLocalPort = null;
            Log.info("Server started alone.");
        } else {
            final Server leader = this.findLeader();
            if (leader == null) {
                this.isLeader = true;
                this.currentLeaderLocalPort = null;
                Log.info("Server started as future new leader, waiting for INIT from current leader");
            } else {
                this.isLeader = false;
                this.currentLeaderLocalPort = leader.getPort();
                Log.info("Server started as lambda server, waiting for INIT from current leader");
            }
        }
    }

    /**
     * Attempts connections to all servers listed in the configuration file
     * and registers online servers.
     *
     * @return the amount of successful connections
     */
    private int connectServers() {
        int successCount = 0;
        for (final Entry<Integer, ServerInfo> entry : this.config.getServers().entrySet()) {
            if (entry.getKey() == this.ownId) {
                // This server's configuration entry, ignore
                continue;
            }
            final ServerInfo info = entry.getValue();
            final Socket s = new Socket();
            try {
                s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                try {
                    this.newConnection(new Server(entry.getKey(), s.getLocalPort()), s);
                    successCount++;
                } catch (final IOException e) {
                    Log.error("Failed to create SocketHandler for server " + info.getId(), e);
                }
            } catch (final IOException e) {
                Log.info("Failed to connect to server " + info.getId() + ", ignoring");
                Log.trace("Error was:", e);
            }
        }
        return successCount;
    }

    /**
     * Finds the current leader in the list of online servers if it's a
     * remote server.
     *
     * @return the current leader if it's a remote server, null otherwise
     */
    private Server findLeader() {
        Server leader = null;
        int lowestOnlineId = this.ownId;
        for (final Peer p : this.connectedPeers.values()) {
            final Server server = (Server) p;
            if (server.getId() < lowestOnlineId) {
                leader = server;
                lowestOnlineId = leader.getId();
            }
        }
        return leader;
    }

    /**
     * Broadcasts a Message to every servers by either sending it to the
     * current leader if this server isn't the leader, or by sending it
     * to all online servers if it is.
     *
     * @param message the message
     */
    public void broadcast(final Message message) {
        if (this.isLeader) {
            for (final int port : this.connectedPeers.keySet()) {
                this.connections.get(port).write(message.toString() + '\n');
            }
        } else {
            this.connections.get(this.currentLeaderLocalPort).write(message.toString() + '\n');
        }
    }

    public Collection<SocketHandler> getAllConnections() {
        return this.connections.values();
    }
}
