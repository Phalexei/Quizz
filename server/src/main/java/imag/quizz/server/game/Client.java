package imag.quizz.server.game;

import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.server.network.ServerConnectionManager;

/**
 * A connected client : player or server
 */
public abstract class Client {

    /**
     * The Client's port.
     */
    private int port;

    /**
     * The Client's socket handler
     */
    private SocketHandler socketHandler;

    /**
     * True if we are currently connected to this client
     */
    private boolean connected;

    /**
     * The Client's ID
     */
    private int id;

    protected final ServerConnectionManager connectionManager;

    public abstract void receive(Message message);

    public enum Type {
        SERVER,
        PLAYER
    }

    public Client(ServerConnectionManager connectionManager, SocketHandler socketHandler, int id) {
        if (socketHandler != null) {
            this.socketHandler = socketHandler;
            this.connected = true;
            this.port = socketHandler.getPort();
        } else {
            this.connected = false;
        }
        this.id = id;
        this.connectionManager = connectionManager;
    }

    public void disconnect() {
        this.connected = false;
        this.disconnected();
    }

    /**
     * Called when the Socket of this Client got disconnected
     */
    protected abstract void disconnected();

    public boolean isConnected() {
        return this.connected;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public void connect(SocketHandler socketHandler) {
        if (this.connected) {
            //TODO: wtf ?
        }
        this.connected = true;
        this.socketHandler = socketHandler;

    }

    public void send(Message message) {
        if (this.connected) {
            message.setSenderId(this.connectionManager.getLocalServerId());
            this.socketHandler.write(message.toString() + "\n");
        }
    }

    public abstract Type getType();
}
