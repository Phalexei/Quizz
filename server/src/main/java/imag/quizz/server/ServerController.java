package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.protocol.message.PingMessage;
import imag.quizz.common.protocol.message.PongMessage;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.Server;
import imag.quizz.server.network.ServerConnectionManager;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Ribesg.
 */
public class ServerController extends MessageHandler implements Controller {

    private final ServerConnectionManager connectionManager;

    private final int    ownId;
    private final Config config;

    private final PingPongTask pingPongTask;

    // Server ID ; Server
    private final SortedMap<Integer, Server> servers;

    /**
     * Constructor.
     */
    protected ServerController(final int ownId, final Config config) {
        super("ServerController");
        this.connectionManager = new ServerConnectionManager(this, ownId, config);

        this.ownId = ownId;
        this.config = config;

        this.servers = new TreeMap<>();

        this.pingPongTask = new PingPongTask(this, this.config);
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
        if (this.connectionManager.linksToPeer(localPort)) {
            // Known Server
            final Server server = (Server) this.connectionManager.getLinkedPeer(localPort);
            switch (message.getCommand()) {
                // TODO Remove useless stuff and implement all the things
                case PING:
                    socketHandler.write(new PongMessage(this.ownId, message).toString());
                    break;
                case PONG:
                    // TODO Validate?
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
            }
        } else {
            // Unknown Server: new incoming connection
            switch (message.getCommand()) {
                // TODO Remove useless stuff and implement all the things
                case PING:
                    break;
                case PONG:
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
            }
        }
    }
}
