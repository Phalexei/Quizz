package imag.quizz.server;

import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.protocol.message.*;
import imag.quizz.server.game.Game;
import imag.quizz.server.game.Player;
import imag.quizz.server.network.PlayerConnectionManager;
import imag.quizz.server.tool.IdGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PlayerController extends MessageHandler implements Controller {

    private static final Random RANDOM = new Random();

    private final ServerController        serverController;
    private final PlayerConnectionManager connectionManager;

    private final long ownId;

    private final PingPongTask pingPongTask;

    /**
     * Constructor.
     *
     * @param serverController the Server controller
     * @param ownId this server's identifier
     */
    public PlayerController(final ServerController serverController, final int localPlayerPort, final long ownId) {
        super("PlayerController");
        this.serverController = serverController;
        this.connectionManager = new PlayerConnectionManager(this, localPlayerPort, ownId);

        this.ownId = ownId;

        this.pingPongTask = new PingPongTask(this, 3_000);
        this.pingPongTask.start();
    }

    @Override
    public void pingTimeout(final int port) {
        // TODO
    }

    @Override
    public void ping(final int port) {
        // TODO
    }

    @Override
    public void handleMessage(final SocketHandler socketHandler, final Message message) {
        final int localPort = socketHandler.getSocket().getLocalPort();
        if (this.connectionManager.linksToPeer(localPort)) {
            // Known and logged in Player
            final Player player = (Player) this.connectionManager.getLinkedPeer(localPort);
            switch (message.getCommand()) {
                case ANSWER:
                    // TODO Check current game and question
                    // TODO Update and continue game
                    break;
                case DROP:
                    // TODO Check current game
                    // TODO Update and end game
                    break;
                case GAMES:
                    this.connectionManager.send(localPort, new GamesMessage(this.ownId, this.buildGamesData(player, this.serverController.getGames().getByPlayer(player))));
                    break;
                case LOGIN:
                    this.login(localPort, socketHandler, message);
                    break;
                case LOGOUT:
                    this.serverController.leaderBroadcast(message);
                    player.setLoggedIn(false);
                    player.setPort(-1);
                    break;
                case NEW:
                    final NewMessage newMessage = (NewMessage) message;
                    final String opponentLogin = newMessage.getOpponent();
                    if (opponentLogin == null) {
                        if (this.serverController.getPlayers().size() == 1) {
                            // No opponent available
                            this.connectionManager.send(player, new NokMessage(this.ownId)); // TODO Error code?
                        } else {
                            final ArrayList<Player> potentialOpponents = new ArrayList<>(this.serverController.getPlayers().values());
                            potentialOpponents.remove(player);
                            final Player opponent = potentialOpponents.get(PlayerController.RANDOM.nextInt(potentialOpponents.size()));
                            this.newGame(player, opponent);
                        }
                    } else {
                        final Player opponent = this.serverController.getPlayers().get(opponentLogin);
                        if (opponent == null) {
                            this.connectionManager.send(player, new NokMessage(this.ownId)); // TODO Error code?
                        } else {
                            this.newGame(player, opponent);
                        }
                    }
                    break;
                case NOANSWER:
                    // TODO Check current game and question
                    // TODO Update and continue game
                    break;
                case PING:
                    this.connectionManager.send(player, new PongMessage(this.ownId, message));
                    break;
                case PLAY:
                    // TODO Check Game id, send THEMES, QUESTION or WAIT to Player
                    break;
                case PONG:
                    this.pingPongTask.pong(localPort);
                    break;
                case REGISTER:
                    this.register(localPort, socketHandler, message);
                    break;
                case THEME:
                    // TODO Check current game and theme status
                    // TODO Update and continue game
                    break;
                default:
                    this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    break;
            }
        } else {
            // Unknown Player new connection or logged out client
            switch (message.getCommand()) {
                case LOGIN:
                    this.login(localPort, socketHandler, message);
                    break;
                case REGISTER:
                    this.register(localPort, socketHandler, message);
                    break;
                default:
                    this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    break;
            }
        }
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
     * @param games the player's games
     *
     * @return a String representation of this player's games dedicated to
     * this player
     */
    public String buildGamesData(final Player player, final Set<Game> games) {
        if (games == null) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();

        final Iterator<Game> it = games.iterator();
        while (it.hasNext()) {
            final Game game = it.next();
            final boolean isPlayerA = game.getPlayerA() == player;
            builder.append(game.getOpponent(player).getLogin()).append(Separator.LEVEL_2);
            builder.append((isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus()) == Game.PlayerStatus.WAIT).append(Separator.LEVEL_2);
            builder.append(isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore()).append(Separator.LEVEL_2);
            builder.append(isPlayerA ? game.getCurrentQuestionA() : game.getCurrentQuestionB()).append(Separator.LEVEL_2);
            builder.append(!isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore()).append(Separator.LEVEL_2);
            builder.append(!isPlayerA ? game.getCurrentQuestionA() : game.getCurrentQuestionB()).append(Separator.LEVEL_2);
            if (it.hasNext()) {
                builder.append(Separator.LEVEL_1);
            }
        }

        return builder.toString();
    }

    private void login(final int localPort, final SocketHandler socketHandler, final Message message) {
        final LoginMessage loginMessage = (LoginMessage) message;
        final String loginMessageLogin = loginMessage.getLogin();
        final String hashedPassword = loginMessage.getHashedPassword();
        if (!this.serverController.getPlayers().containsKey(loginMessageLogin)) {
            this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
        } else {
            final Player player = this.serverController.getPlayers().get(loginMessageLogin);
            if (player.getPasswordHash().equals(hashedPassword)) {
                player.setLoggedIn(true);
                this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
                this.connectionManager.send(localPort, new GamesMessage(this.ownId, this.buildGamesData(player, this.serverController.getGames().getByPlayer(player))));
            } else {
                this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
            }
        }
    }

    private void register(final int localPort, final SocketHandler socketHandler, final Message message) {
        final RegisterMessage registerMessage = (RegisterMessage) message;
        final String registerMessageLogin = registerMessage.getLogin();
        if (this.serverController.getPlayers().containsKey(registerMessageLogin)) {
            this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
        } else {
            final long id = IdGenerator.nextPlayer(); // TODO Globalize that, ask leader
            final Player player = new Player(id, localPort, registerMessageLogin, registerMessage.getHashedPassword());
            this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
            this.serverController.getPlayers().put(registerMessageLogin, player);
            this.connectionManager.send(player, new OkMessage(this.ownId));
        }
    }

    private void newGame(final Player player, final Player opponent) {
        final Game game = this.serverController.getGames().newGame(player, opponent);
        this.connectionManager.send(player, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
        if (opponent.getPort() != -1) {
            this.connectionManager.send(opponent, new ThemesMessage(this.ownId, game.getId(), game.getThemesB()));
        }
        // TODO Broadcast GAME Message
    }

    @Override
    public void lostConnection(final SocketHandler socketHandler) {
        this.connectionManager.forgetConnection(socketHandler.getSocket().getLocalPort());
        // TODO Broadcast logout
        // TODO Maybe other things
    }
}
