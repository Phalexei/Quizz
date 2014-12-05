package imag.quizz.client.game;

import imag.quizz.client.network.ConnectionManager;
import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.protocol.message.PingMessage;
import imag.quizz.common.tool.Log;

/**
 * Created by Ribesg.
 */
public class ClientController extends MessageHandler implements Controller {

    private Window window;
    private final Config config;
    private final ConnectionManager connectionManager;
    private final PingPongTask pingPongTask;
    private int playerId;

    public ClientController(final Config config) {
        super("Controller");
        this.window = null;
        this.config = config;
        this.connectionManager = new ConnectionManager(config, this);
        this.playerId = -1; // -1 is invalid : no ID yet
        this.pingPongTask = new PingPongTask(this, 5_000);
        this.pingPongTask.start();
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    public void onButtonClick(final String textClicked) {
        Log.info("Réponse sélectionnée : \"" + textClicked + '"');
        this.window.lockButtons();
    }

    @Override
    public void handleMessage(SocketHandler socketHandler, Message message) {
        switch (message.getCommand()) {
            //TODO: fill in each case
            case PING:
                break;
            case PONG:
                System.out.println("PONG RECEIVED !");
                this.pingPongTask.pong(socketHandler.getSocket().getLocalPort());
                break;
            case OK:
                break;
            case NOK:
                break;
            case INIT:
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
                // TODO KEK
        }
    }

    @Override
    public void lostConnection(SocketHandler socketHandler) {
        this.pingPongTask.removePort(this.connectionManager.getSocketHandler().getSocket().getLocalPort());
        this.connectionManager.lostConnection();
        this.connect();
    }

    @Override
    public void pingTimeout(int port) {
        this.lostConnection(null);
    }

    @Override
    public void ping(int port) {
        if (this.playerId != -1) {
            this.connectionManager.send(new PingMessage(this.playerId));
        }
    }

    public void connect() {
        try {
            this.connectionManager.tryConnect();
            this.pingPongTask.addPort(this.connectionManager.getSocketHandler().getSocket().getLocalPort());
        } catch (ConnectionManager.NoServerException e) {
            this.window.noConnection();
        }
    }
}
