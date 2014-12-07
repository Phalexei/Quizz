package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.protocol.message.*;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.*;
import imag.quizz.server.network.ServerConnectionManager;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class ServerController extends MessageHandler implements Controller {

    private final ServerConnectionManager connectionManager;

    private final long   ownId;
    private final Config config;

    private boolean isLeader;
    private long    currentLeaderId;
    private Integer currentLeaderLocalPort;

    private final PingPongTask pingPongTask;

    // Server ID ; Server
    private final SortedMap<Integer, Server> servers;
    private final Map<String, Player>        players;
    private final Games                      games;

    private boolean initialized;

    /**
     * Constructor.
     */
    protected ServerController(final long ownId, final Config config, final QuestionBase questionBase) {
        super("ServerController");
        this.ownId = ownId;
        this.config = config;
        this.servers = new TreeMap<>();
        this.players = new HashMap<>();
        this.games = new Games(questionBase);

        this.initialized = false;

        this.connectionManager = new ServerConnectionManager(this, ownId);
        this.pingPongTask = new PingPongTask(this, 3_000);
        this.pingPongTask.start();
    }

    @Override
    public void pingTimeout(final int port) {
        Log.warn("Ping timeout!"); // TODO
    }

    @Override
    public void ping(final int port) {
        this.connectionManager.send(port, new PingMessage(this.ownId));
    }

    @Override
    public void handleMessage(final SocketHandler socketHandler, final Message message) {
        final int localPort = socketHandler.getSocket().getLocalPort();
        // Known Server
        Server server = (Server) this.connectionManager.getLinkedPeer(localPort);

        if (server == null) { //server unknown for now, get his ID and register him to the connection manager
            server = new Server(message.getSenderId(), socketHandler.getSocket().getLocalPort());
            this.connectionManager.learnConnectionPeerIdentity(server, socketHandler);
        }

        switch (message.getCommand()) {
            // TODO Remove useless stuff and implement all the things
            case PING:
                socketHandler.write(new PongMessage(this.ownId, message).toString());
                break;
            case PONG:
                this.pingPongTask.pong(localPort);
                break;
            case OK:
                // Handle Leader switch
                if (message.getSenderId() < this.currentLeaderId) {
                    Log.info((this.currentLeaderId == this.ownId ? "We were leader" : "Leader was server " + this.currentLeaderId)
                                     + ", the leader is now server " + message.getSenderId());
                    this.isLeader = false;
                    this.currentLeaderId = message.getSenderId();
                    this.currentLeaderLocalPort = socketHandler.getSocket().getLocalPort();
                }
                // TODO Other things
                break;
            case NOK:
                break;
            case INIT:
                if (this.initialized) {
                    Log.warn("Received INIT message from server " + message.getSenderId() + " while already being initialized");
                    this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                } else {
                    Log.info("Received INIT from current leader with ID " + message.getSenderId());
                    this.loadInitData(((InitMessage) message).getData());
                    this.initialized = true;
                    this.connectionManager.connectServers();
                    this.connectionManager.broadcast(new OkMessage(this.ownId));
                }
                break;
            case REGISTER:
                break;
            case LOGIN:
                break;
            case GAMES:
                break;
            case NEW:
                break;
            case GAME:
                break;
            case PLAY:
                break;
            case THEMES:
                break;
            case THEME:
                break;
            case QUESTION:
                break;
            case ANSWER:
                break;
            case NOANSWER:
                break;
            case WAIT:
                break;
            case DROP:
                break;
            case END:
                break;
            default:
                this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                break;
        }
    }

    public String buildInitData() {
        final boolean players = !this.players.isEmpty();
        final boolean games = !this.games.getGames().isEmpty();

        final StringBuilder builder = new StringBuilder();

        if (players) {
            final Iterator<Player> itPlayer = this.players.values().iterator();
            while (itPlayer.hasNext()) {
                builder.append(itPlayer.next().toMessageData(3));
                if (itPlayer.hasNext()) {
                    builder.append(Separator.LEVEL_2);
                }
            }
        } else {
            builder.append("null");
        }
        builder.append(Separator.LEVEL_1);
        if (games) {
            final Iterator<Game> itGame = this.games.getGames().values().iterator();
            while (itGame.hasNext()) {
                builder.append(itGame.next().toMessageData(3));
                if (itGame.hasNext()) {
                    builder.append(Separator.LEVEL_2);
                }
            }
        } else {
            builder.append("null");
        }
        return builder.toString();
    }

    public void loadInitData(final String data) {
        final String[] split = data.split(Separator.LEVEL_1);
        Validate.isTrue(split.length == 2);
        this.loadInitPlayers(split[0]);
        this.loadInitGames(split[1]);
    }

    private void loadInitPlayers(final String playersData) {
        if ("null".equals(playersData)) {
            return;
        }
        final String[] split = playersData.split(Separator.LEVEL_2);
        for (final String playerString : split) {
            final Player player = Player.fromMessageData(playerString, 3);
            this.players.put(player.getLogin(), player);
        }
    }

    private void loadInitGames(final String gamesData) {
        if ("null".equals(gamesData)) {
            return;
        }
        final String[] split = gamesData.split(Separator.LEVEL_2);
        for (final String gameString : split) {
            final Game game = Game.fromMessageData(this.players, gameString, 3);
            this.games.getGames().put(game.getId(), game);
        }
    }

    @Override
    public void lostConnection(final SocketHandler socketHandler) {
        this.connectionManager.forgetConnection(socketHandler.getSocket().getLocalPort());
        // TODO Update leader eventually
        // TODO Maybe other things
    }

    public long getOwnId() {
        return this.ownId;
    }

    public Config getConfig() {
        return this.config;
    }

    public boolean isLeader() {
        return this.isLeader;
    }

    public long getCurrentLeaderId() {
        return this.currentLeaderId;
    }

    public Integer getCurrentLeaderLocalPort() {
        return this.currentLeaderLocalPort;
    }

    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public Games getGames() {
        return this.games;
    }

    public void setLeader(final boolean isLeader) {
        this.isLeader = isLeader;
    }

    public void setCurrentLeaderId(final long currentLeaderId) {
        this.currentLeaderId = currentLeaderId;
    }

    public void setCurrentLeaderLocalPort(final Integer currentLeaderLocalPort) {
        this.currentLeaderLocalPort = currentLeaderLocalPort;
    }
}
