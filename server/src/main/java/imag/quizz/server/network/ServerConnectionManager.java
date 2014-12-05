package imag.quizz.server.network;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.Server;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map.Entry;

/**
 * Handles connections
 */
public class ServerConnectionManager extends ConnectionManager {

    private final int    ownId;
    private final Config config;

    private boolean isLeader;
    private int     currentLeaderId;
    private Integer currentLeaderLocalPort;

    private boolean isConnectedToLambdas;

    public ServerConnectionManager(final MessageHandler messageHandler, final int ownId, final Config config) {
        super(messageHandler, false, config.getServers().get(ownId).getServerPort());
        this.ownId = ownId;
        this.config = config;

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
            this.isLeader = true;
            Log.info("Server started alone.");
        } else {
            if (this.currentLeaderId > this.ownId) {
                this.isLeader = false;
                Log.info("Server started as future new leader, waiting for INIT from current leader");
            } else {
                this.isLeader = false;
                this.currentLeaderLocalPort = port;
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
                    this.currentLeaderId = entry.getKey();
                    return s.getLocalPort();
                } catch (final IOException e) {
                    Log.error("Failed to create SocketHandler for server " + info.getId(), e);
                }
            } catch (final IOException e) {
                Log.info("Failed to connect to server " + info.getId() + ", ignoring");
                Log.trace("Error was:", e);
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
        if (this.currentLeaderId > this.ownId) {
            this.isLeader = true;
            this.currentLeaderId = this.ownId;
            this.currentLeaderLocalPort = null;
        }
        int successCount = 0;
        for (final Entry<Integer, ServerInfo> entry : this.config.getServers().entrySet()) {
            if (entry.getKey() == this.ownId || entry.getKey() == this.currentLeaderId) {
                // Ignore
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
        this.isConnectedToLambdas = true;
        return successCount;
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
}
