package imag.quizz.server;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.server.game.Game;
import imag.quizz.server.game.Peer;
import imag.quizz.server.game.Player;
import imag.quizz.server.game.QuestionBase;
import imag.quizz.server.network.PlayerConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Ribesg.
 */
public class PlayerController extends MessageHandler implements Controller {

    private final ServerController        serverController;
    private final PlayerConnectionManager connectionManager;

    private final QuestionBase questionBase;

    // Player Login ; Player
    private final Map<String, Player> players;

    // Game ID ; Game
    private final SortedMap<Long, Game> games;

    /**
     * Constructor.
     *
     * @param serverController the Server controller
     */
    public PlayerController(final ServerController serverController, final int localPlayerPort, final QuestionBase questionBase) {
        super("PlayerController");
        this.serverController = serverController;
        this.connectionManager = new PlayerConnectionManager(this, localPlayerPort);

        this.questionBase = questionBase;

        this.players = new HashMap<>();
        this.games = new TreeMap<>();
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
                    // TODO Pong
                    break;
                case PLAY:
                    // TODO Check Game id, send THEMES, QUESTION or WAIT to Player
                    break;
                case PONG:
                    // TODO Check value with PING
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
                    // TODO Check and login
                    break;
                case REGISTER:
                    // TODO Check and register
                    break;
                default:
                    // TODO Refuse message as it is invalid
                    break;
            }
        }
    }
}
