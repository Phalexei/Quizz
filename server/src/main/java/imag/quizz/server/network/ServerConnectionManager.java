package imag.quizz.server.network;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handles connections
 */
public class ServerConnectionManager {

    private final int                         serverId;
    private final Map<Integer, SocketHandler> clients;
    private final Map<Integer, SocketHandler> servers;
    private final MessageHandler              handler;
    private final Config                      config;

    private final ServerSocketChecker serverServerSocketChecker;
    private final ServerSocketChecker clientServerSocketChecker;

    public ServerConnectionManager(final MessageHandler handler, final Config config, final int serverId) {
        this.clients = new HashMap<>();
        this.servers = new HashMap<>();
        this.handler = handler;
        this.config = config;
        this.serverId = serverId;

        final ServerInfo info = this.config.getServers().get(this.serverId);

        this.serverServerSocketChecker = new ServerSocketChecker(this.handler, this.servers, info.getServerPort(), false);
        this.clientServerSocketChecker = new ServerSocketChecker(this.handler, this.clients, info.getClientPort(), true);

        this.serverServerSocketChecker.start();
        this.clientServerSocketChecker.start();

        this.connectServers();
    }

    private void connectServers() {
        for (final Entry<Integer, ServerInfo> entry : this.config.getServers().entrySet()) {
            if (entry.getKey() == this.serverId) {
                continue;
            }
            final ServerInfo info = entry.getValue();
            final Socket s = new Socket();
            try {
                s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                try {
                    this.servers.put(entry.getKey(), new SocketHandler(s, this.handler));
                } catch (final IOException e) {
                    Log.error("Failed to create SocketHandler for server " + info.getId(), e);
                }
            } catch (final IOException e) {
                Log.info("Failed to connect to server " + info.getId() + ", ignoring");
                Log.debug("Error was:", e);
            }
        }
    }

    private void broadcast(final String message) {
        for (final Entry<Integer, SocketHandler> entry : this.servers.entrySet()) {
            if (entry.getKey() != this.serverId && entry.getValue().isReady()) {
                entry.getValue().write(message);
            }
        }
    }

    public void stop() {
        this.serverServerSocketChecker.interrupt();
        this.clientServerSocketChecker.interrupt();
    }

    /**
     * This method checks if a player is connected to this server
     * @param playerID : the numeric ID of the player
     * @return true if player is connected to this server
     */
    public boolean isConnected(final int playerID) {
        throw new NotImplementedException("TODO");
    }

    public int getClientPort(final int playerId) {
        throw new NotImplementedException("TODO");
    }
}
