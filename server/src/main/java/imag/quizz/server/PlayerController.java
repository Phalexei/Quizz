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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerController extends MessageHandler implements Controller {

    private final ServerController        serverController;
    private final PlayerConnectionManager connectionManager;

    private final int ownId;

    private final PingPongTask pingPongTask;

    // Player Login ; Player
    private final Map<String, Player> players;

    private final Games games;

    /**
     * Constructor.
     *
     * @param serverController the Server controller
     * @param ownId this server's identifier
     */
    public PlayerController(final ServerController serverController, final int localPlayerPort, final QuestionBase questionBase, final int ownId) {
        super("PlayerController");
        this.serverController = serverController;
        this.connectionManager = new PlayerConnectionManager(this, localPlayerPort, ownId);

        this.ownId = ownId;

        this.players = new HashMap<>();
        this.games = new Games(questionBase);

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
                    // TODO Send Games to player
                    break;
                case LOGIN:
                    // TODO Check that Player has logged out
                    // TODO If not, log him out and attempt login
                    // TODO Change Peer associated to connection
                    break;
                case NEW:
                    // TODO Check for argument and available opponent
                    // TODO Eventually start a new game
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
                    // TODO Check that Player has logged out
                    // TODO If not, log him out and attempt register
                    // TODO Change Peer associated to connection
                    break;
                case THEME:
                    // TODO Check current game and theme status
                    // TODO Update and continue game
                    break;
                default:
                    // TODO Invalid message
                    break;
            }
        } else {
            // Unknown Player new connection or logged out client
            switch (message.getCommand()) {
                case LOGIN:
                    final LoginMessage loginMessage = (LoginMessage) message;
                    final String loginMessageLogin = loginMessage.getLogin();
                    final String hashedPassword = loginMessage.getHashedPassword();
                    if (!this.players.containsKey(loginMessageLogin)) {
                        this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    } else {
                        final Player player = this.players.get(loginMessageLogin);
                        if (player.getPasswordHash().equals(hashedPassword)) {
                            player.setLoggedIn(true);
                            player.setPort(localPort);
                            final Set<Game> playerGames = this.games.getByPlayer(player);
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
                    if (this.players.containsKey(registerMessageLogin)) {
                        this.connectionManager.send(localPort, new NokMessage(this.ownId)); // TODO Error code?
                    } else {
                        final long id = IdGenerator.nextPlayer();
                        final Player player = new Player(id, localPort, registerMessageLogin, registerMessage.getHashedPassword());
                        this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
                        this.players.put(registerMessageLogin, player);
                        this.connectionManager.send(player, new OkMessage(this.ownId));
                    }
                    break;
                default:
                    // TODO Refuse message as it is invalid
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
