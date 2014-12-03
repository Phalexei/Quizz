package imag.quizz.server.network;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.InitMessage;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.server.ServerMessageHandler;
import imag.quizz.server.game.Client;
import imag.quizz.server.game.Player;
import imag.quizz.server.game.Server;
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
    private final Map<Integer, SocketHandler> playerSockets;
    private final Map<Integer, SocketHandler> serverSockets;
    private final Map<Integer, Player>        players;
    private final Map<Integer, Server>        servers;
    private final ServerMessageHandler        messageHandler;
    private final Config                      config;
    private boolean                           isLeader;

    private final ServerSocketChecker serverServerSocketChecker;
    private final ServerSocketChecker playerServerSocketChecker;

    public ServerConnectionManager(final ServerMessageHandler messageHandler, final Config config, final int serverId) {
        this.playerSockets = new HashMap<>();
        this.serverSockets = new HashMap<>();
        this.messageHandler = messageHandler;
        this.messageHandler.registerConnectionManager(this);
        this.config = config;
        this.serverId = serverId;

        final ServerInfo info = this.config.getServers().get(this.serverId);

        this.serverServerSocketChecker = new ServerSocketChecker(this, info.getServerPort(), false, messageHandler);
        this.playerServerSocketChecker = new ServerSocketChecker(this, info.getPlayerPort(), true, messageHandler);

        this.serverServerSocketChecker.start();
        this.playerServerSocketChecker.start();

        this.servers = new HashMap<>();
        this.players = new HashMap<>();

        this.initialize();

    }

    private void initialize() {
        int serversOnline = this.connectServers();

        for (ServerInfo serverInfo : this.config.getServers().values()) {
            final int id = serverInfo.getId();
            this.servers.put(id, new Server(this, this.serverSockets.get(id), id));
        }

        if (isLeader(this.serverId)) { // we are the new leader
            if (serversOnline == 0) { // we are alone
                Log.info("Server started alone, we are leader");
            } else { // we are not alone, wait for INIT from previous leader
                Log.info("Server started as new leader, waiting for INIT from current leader");
            }
        } else {
            Log.info("Server started as lambda server, waiting for INIT from current leader");
        }
    }

    /**
     * Connects this server to all servers listed in the config
     * @return number of successful connections
     */
    private int connectServers() {
        int successCount = 0;
        for (final Entry<Integer, ServerInfo> entry : this.config.getServers().entrySet()) {
            if (entry.getKey() == this.serverId) {
                continue;
            }
            final ServerInfo info = entry.getValue();
            final Socket s = new Socket();
            try {
                s.connect(new InetSocketAddress(info.getHost(), info.getServerPort()));
                try {
                    this.serverSockets.put(entry.getKey(), new SocketHandler(s, this.messageHandler));
                    successCount++;
                } catch (final IOException e) {
                    Log.error("Failed to create SocketHandler for server " + info.getId(), e);
                }
            } catch (final IOException e) {
                Log.info("Failed to connect to server " + info.getId() + ", ignoring");
                Log.debug("Error was:", e);
            }
        }
        return successCount;
    }

    /**
     * Register a new incoming connection
     * @param playerConnection true if this a new player, false if it is a server
     * @param socketHandler the handler associated to this new socket
     * @param port the port of the socket
     */
    public void addNewConnection(boolean playerConnection, SocketHandler socketHandler, int port) {
        if (playerConnection) { // new client
            if (playerSockets.get(port) != null) {
                //TODO: wtf?
            }
            playerSockets.put(port, socketHandler);
            //TODO: we do not know who the player is...
            // => first message he sends us will contain his ID...
        } else { // new server
            if (serverSockets.get(port) != null) {
                //TODO: wtf?
            }
            serverSockets.put(port, socketHandler);

            final Server s = getServerByPort(port);
            if (s == null) {
                // TODO: find ID of server that connected to us. HOW ? :'(
                // => first message he sends us will contain his ID...
                //s = new Server(socketHandler, ID)
            }
            if (s != null) {
                s.connect(socketHandler);
                if (s.getId() < this.serverId && isLeader(this.serverId)) {
                    //TODO: s is the future new leader. Send him INIT
                    s.send(new InitMessage(this.serverId));
                    Log.info("Sending INIT to new leader");
                }
            }
        }
    }

    public Client getClientByPort(int port) {
        Client client = getServerByPort(port);
        if (client == null) {
            client = getPlayerByPort(port);
        }
        return client;
    }

    private Server getServerByPort(int port) {
        for (Server s : servers.values()) {
            if (s.getPort() == port) {
                return s;
            }
        }
        return null;
    }

    private Player getPlayerByPort(int port) {
        for (Player p : players.values()) {
            if (p.getPort() == port) {
                return p;
            }
        }
        return null;
    }

    public Player getPlayerById(int playerId) {
        return this.players.get(playerId);
    }

    private Server getLeader() {
        Server leader = null;

        for (ServerInfo serverInfo : config.getServers().values()) {
            final Server server = servers.get(serverInfo.getId());
            if (server != null && (server.isConnected() || server.getId() == this.serverId)) {
                leader = server;
                break;
            }
        }
        return leader;
    }

    /**
     * @param serverId the id to check
     * @return true if the specified server is currently the leader
     */
    public boolean isLeader(int serverId) {
        final Server leader = getLeader();
        return leader != null && leader.getId() == serverId;
    }

    /**
     * Sends a message to the current leader
     * @param message the message to send
     */
    public void sendToLeader(final Message message) {
        final Server leader = getLeader();
        if (leader.getId() != this.serverId) {
            leader.send(message);
        }
    }

    /**
     * Broadcasts a message to all connected servers
     * @param message the message to broadcast
     */
    public void broadcast(final Message message) {
        for (Server s : servers.values()) {
            if (s.getId() != this.serverId) {
                s.send(message);
            }
        }
    }

    public void stop() {
        this.serverServerSocketChecker.interrupt();
        this.playerServerSocketChecker.interrupt();
    }

    /**
     * This method checks if a player is connected to this server
     * @param playerID : the numeric ID of the player
     * @return true if player is connected to this server
     */
    public boolean isConnected(final int playerID) {
        return (players.get(playerID) != null);
    }

    public int getClientPort(final int playerID) {
        if (isConnected(playerID)) {

        }
        throw new NotImplementedException("TODO");
    }

    public void sendToClient(final int playerID, Message message) {
        Player player = players.get(playerID);
        if (player != null) {
            player.send(message);
        }
    }


    public int getLocalServerId() {
        return this.serverId;
    }
}
