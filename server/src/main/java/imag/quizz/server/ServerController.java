package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.protocol.message.*;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.SockUri;
import imag.quizz.server.game.*;
import imag.quizz.server.game.Game.PlayerStatus;
import imag.quizz.server.game.QuestionBase.Question;
import imag.quizz.server.network.PlayerConnectionManager;
import imag.quizz.server.network.ServerConnectionManager;
import imag.quizz.server.tool.IdGenerator;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class ServerController extends MessageHandler implements Controller {

    private final ServerConnectionManager connectionManager;
    private       PlayerConnectionManager playerConnectionManager;

    private final long   ownId;
    private final Config config;

    private boolean isLeader;
    private long    currentLeaderId;
    private String  currentLeaderUri;

    private final PingPongTask pingPongTask;

    // Server ID ; Server
    private final SortedMap<Long, Server> servers;
    private final Map<String, Player>     players;
    private final Games                   games;

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

        this.pingPongTask = new PingPongTask(this, 3_000);
        this.connectionManager = new ServerConnectionManager(this, ownId);
        this.pingPongTask.start();
    }

    public void setPlayerConnectionManager(final PlayerConnectionManager playerConnectionManager) {
        this.playerConnectionManager = playerConnectionManager;
    }

    @Override
    public void pingTimeout(final String uri) {
        Log.warn(uri + " ping timeout!");
        this.connectionManager.killConnection(uri);
    }

    @Override
    public void ping(final String uri) {
        this.connectionManager.send(uri, new PingMessage(this.ownId));
    }

    @Override
    public void handleMessage(final SocketHandler socketHandler, final Message message) {
        final String uri = SockUri.from(socketHandler.getSocket());
        // Known Server
        Server server = (Server) this.connectionManager.getLinkedPeer(uri);

        if (server == null) { //server unknown for now, get his ID and register him to the connection manager
            server = new Server(message.getSourceId(), uri);
            this.servers.put(server.getId(), server);
            this.connectionManager.learnConnectionPeerIdentity(server, socketHandler);
        }

        // Variables used multiple times in the following switch
        final Player player, opponent;
        final Game game;
        final boolean isPlayerA;
        final PlayerStatus status;

        switch (message.getCommand()) {
            case PING:
                socketHandler.write(new PongMessage(this.ownId, message).toString());
                break;
            case PONG:
                this.pingPongTask.pong(uri);
                break;
            case OK:
                // Handle Leader switch
                if (message.getSourceId() < this.currentLeaderId) {
                    Log.info((this.currentLeaderId == this.ownId ? "Ce serveur était leader" : "Le leader était le serveur " + this.currentLeaderId)
                                     + ", le leader est maintenant le serveur " + message.getSourceId());
                    this.isLeader = false;
                    this.currentLeaderId = message.getSourceId();
                    this.currentLeaderUri = uri;
                    server.setLeader(true);
                }
                break;
            case NOK:
                break;
            case INIT:
                if (this.initialized) {
                    Log.warn("Reçu un message INIT du serveur " + message.getSourceId() + " alors qu'on est déjà initializé");
                    this.connectionManager.send(uri, new NokMessage(this.ownId, message));
                } else {
                    Log.info("Reçu un message INIT du leader actuel avec l'ID " + message.getSourceId());
                    this.loadInitData(((InitMessage) message).getData());
                    this.initialized = true;
                    this.connectionManager.broadcast(new OkMessage(this.ownId, message));
                    if (message.getSourceId() > this.getOwnId()) {
                        this.isLeader = true;
                        this.currentLeaderId = this.ownId;
                        this.currentLeaderUri = null;
                        Log.info("Nous sommes maintenant leader");
                    }
                }
                break;
            case REGISTER:
                final RegisterMessage registerMessage = (RegisterMessage) message;
                if (this.isLeader) {
                    final long id = IdGenerator.nextPlayer();
                    player = new Player(id, null, registerMessage.getLogin(), registerMessage.getHashedPassword());
                    this.players.put(player.getLogin(), player);
                    registerMessage.setSourceId(id);
                    this.leaderBroadcast(registerMessage);
                } else {
                    player = new Player(message.getSourceId(), null, registerMessage.getLogin(), registerMessage.getHashedPassword());
                    this.players.put(player.getLogin(), player);
                }
                break;
            case LOGIN:
                final LoginMessage loginMessage = (LoginMessage) message;
                player = this.players.get(loginMessage.getLogin());
                player.setLoggedIn(true);
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case LOGOUT:
                final LogoutMessage logoutMessage = (LogoutMessage) message;
                player = this.players.get(logoutMessage.getPlayerLogin());
                player.setLoggedIn(false);
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case GAME:
                final GameMessage gameMessage = (GameMessage) message;
                game = Game.fromMessageData(this.players, gameMessage.getGameData());
                this.games.getGames().put(game.getId(), game);
                game.getPlayerA().setCurrentGameId(game.getId());
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case PLAY:
                final PlayMessage playMessage = (PlayMessage) message;
                player = this.getPlayer(playMessage.getSourceId());
                player.setCurrentGameId(playMessage.getGameId());
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case THEME:
                final ThemeMessage themeMessage = (ThemeMessage) message;
                player = this.getPlayer(themeMessage.getSourceId());
                game = this.games.getGames().get(player.getCurrentGameId());
                final boolean opponentUnlocked = game.playerSelectTheme(player, themeMessage.getChosenTheme());
                opponent = game.getOpponent(player);
                if (opponentUnlocked && opponent.getUri() != null) {
                    final String gamesData = this.buildGamesData(opponent);
                    this.playerConnectionManager.send(opponent, new GamesMessage(this.ownId, gamesData));
                }
                final Question question = game.getCurrentQuestion(player);
                this.connectionManager.send(player, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case ANSWER:
                final AnswerMessage answerMessage = (AnswerMessage) message;
                player = this.getPlayer(answerMessage.getSourceId());
                game = this.getGames().getGames().get(player.getCurrentGameId());
                isPlayerA = game.getPlayerA() == player;
                game.playerSelectAnswer(player, answerMessage.getChosenAnswer());
                status = isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus();
                if (status == PlayerStatus.WAIT) {
                    if (game.getCurrentQuestionA() == 9 && game.getCurrentQuestionB() == 9) {
                        final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        opponent = game.getOpponent(player);
                        if (opponent.getUri() != null) {
                            this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                        }
                    }
                }
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case NOANSWER:
                player = this.getPlayer(message.getSourceId());
                game = this.getGames().getGames().get(player.getCurrentGameId());
                isPlayerA = game.getPlayerA() == player;
                game.playerDoesntAnswer(player);
                status = isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus();
                if (status == PlayerStatus.WAIT) {
                    if (game.getCurrentQuestionA() == 9 && game.getCurrentQuestionB() == 9) {
                        final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        opponent = game.getOpponent(player);
                        if (opponent.getUri() != null) {
                            this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                        }
                    }
                }
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            case DROP:
                player = this.getPlayer(message.getSourceId());
                game = this.getGames().getGames().get(player.getCurrentGameId());
                isPlayerA = game.getPlayerA() == player;
                game.playerDrop(player);
                final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                opponent = game.getOpponent(player);
                if (opponent.getUri() != null) {
                    this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                }
                if (this.isLeader) {
                    this.leaderBroadcast(message);
                }
                break;
            default:
                this.connectionManager.send(uri, new NokMessage(this.ownId, "Message inattendu", message));
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

    /**
     * Builds a data String representing Games of a Player.
     *
     * Each game is represented by:
     * - Opponent login
     * - Player has to wait for opponent (true/false)
     * - Player own score
     * - Player current question
     * - Opponent own score
     * - Opponent current question
     *
     * @param player the player
     *
     * @return a String representation of this player's games dedicated to
     * this player
     */
    public String buildGamesData(final Player player) {
        final Set<Game> games = this.getGames().getByPlayer(player);

        final StringBuilder builder = new StringBuilder();

        if (games != null) {
            final Iterator<Game> it = games.iterator();
            while (it.hasNext()) {
                final Game game = it.next();
                final boolean isPlayerA = game.getPlayerA() == player;
                builder.append(game.getId()).append(Separator.LEVEL_2);
                builder.append(game.getOpponent(player).getLogin()).append(Separator.LEVEL_2);
                builder.append((isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus()) == PlayerStatus.WAIT).append(Separator.LEVEL_2);
                builder.append(isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore()).append(Separator.LEVEL_2);
                builder.append(isPlayerA ? game.getCurrentQuestionA() : game.getCurrentQuestionB()).append(Separator.LEVEL_2);
                builder.append(!isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore()).append(Separator.LEVEL_2);
                builder.append(!isPlayerA ? game.getCurrentQuestionA() : game.getCurrentQuestionB()).append(Separator.LEVEL_2);
                if (it.hasNext()) {
                    builder.append(Separator.LEVEL_1);
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
        final String uri = SockUri.from(socketHandler.getSocket());
        this.pingPongTask.removeUri(uri);
        this.connectionManager.forgetConnection(uri);
        boolean newLeaderIsSomebodyElse = false;
        for (final Map.Entry<Long, Server> e : this.servers.entrySet()) {
            final long id = e.getKey();
            final Server server = e.getValue();
            if (uri.equals(server.getUri())) {
                server.setUri(null);
                server.setLeader(false);
                Log.info("Connexion au server " + server.getId() + " perdue");
            } else if (server.getUri() != null) {
                server.setLeader(true);
                newLeaderIsSomebodyElse = true;
                Log.info("Le serveur " + id + " est maintenant leader");
                break;
            }
        }
        if (!newLeaderIsSomebodyElse) {
            this.isLeader = true;
            this.currentLeaderId = this.ownId;
            this.currentLeaderUri = null;
            Log.info("Nous sommes maintenant leader");
        }
    }

    public void leaderBroadcast(final Message message) {
        this.connectionManager.leaderBroadcast(message);
    }

    public Player getPlayer(final long id) {
        for (final Player player : this.players.values()) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
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

    public String getCurrentLeaderUri() {
        return this.currentLeaderUri;
    }

    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public SortedMap<Long, Server> getServers() {
        return this.servers;
    }

    public Games getGames() {
        return this.games;
    }

    public PingPongTask getPingPongTask() {
        return this.pingPongTask;
    }

    public void setLeader(final boolean isLeader) {
        this.isLeader = isLeader;
    }

    public void setCurrentLeaderId(final long currentLeaderId) {
        this.currentLeaderId = currentLeaderId;
    }

    public void setCurrentLeaderUri(final String currentLeaderUri) {
        this.currentLeaderUri = currentLeaderUri;
    }
}
