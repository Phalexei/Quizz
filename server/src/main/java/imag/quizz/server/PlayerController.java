package imag.quizz.server;

import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.message.*;
import imag.quizz.server.game.Game;
import imag.quizz.server.game.Games;
import imag.quizz.server.game.Player;
import imag.quizz.server.game.QuestionBase;
import imag.quizz.server.network.PlayerConnectionManager;
import imag.quizz.server.tool.IdGenerator;

import java.util.*;

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
                    final Set<Game> playerGames = this.serverController.getGames().getByPlayer(player);
                    // TODO this.connectionManager.send(localPort, new GamesMessage(playerGames /* TODO */));
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
                            // TODO Merge following code with code in next else block as it's the same thing
                            final Game game = this.serverController.getGames().newGame(player, opponent);
                            /* TODO
                            this.connectionManager.send(player, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
                            this.connectionManager.send(opponent, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
                            */
                        }
                    } else {
                        final Player opponent = this.serverController.getPlayers().get(opponentLogin);
                        if (opponent == null) {
                            this.connectionManager.send(player, new NokMessage(this.ownId)); // TODO Error code?
                        } else {
                            final Game game = this.serverController.getGames().newGame(player, opponent);
                            /* TODO
                            this.connectionManager.send(player, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
                            this.connectionManager.send(opponent, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
                            */
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
                    final LoginMessage loginMessage = (LoginMessage) message;
                    final String loginMessageLogin = loginMessage.getLogin();
                    final String hashedPassword = loginMessage.getHashedPassword();
                    if (!this.serverController.getPlayers().containsKey(loginMessageLogin)) {
                        this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    } else {
                        final Player player = this.serverController.getPlayers().get(loginMessageLogin);
                        if (player.getPasswordHash().equals(hashedPassword)) {
                            player.setLoggedIn(true);
                            player.setPort(localPort);
                            final Set<Game> playerGames = this.serverController.getGames().getByPlayer(player);
                            // TODO if (playerGames == null) {
                            this.connectionManager.send(localPort, new OkMessage(this.ownId));
                            // TODO } else {
                            // TODO     this.connectionManager.send(localPort, new GamesMessage(playerGames /* TODO */));
                            // TODO }
                        } else {
                            this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                        }
                    }
                    break;
                case REGISTER:
                    final RegisterMessage registerMessage = (RegisterMessage) message;
                    final String registerMessageLogin = registerMessage.getLogin();
                    if (this.serverController.getPlayers().containsKey(registerMessageLogin)) {
                        this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    } else {
                        final long id = IdGenerator.nextPlayer();
                        final Player player = new Player(id, localPort, registerMessageLogin, registerMessage.getHashedPassword());
                        this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
                        this.serverController.getPlayers().put(registerMessageLogin, player);
                        this.connectionManager.send(player, new OkMessage(this.ownId));
                    }
                    break;
                default:
                    this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    break;
            }
        }
    }

    @Override
    public void lostConnection(final SocketHandler socketHandler) {
        this.connectionManager.forgetConnection(socketHandler.getSocket().getLocalPort());
        // TODO Broadcast logout
        // TODO Maybe other things
    }
}
