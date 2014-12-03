package imag.quizz.server.game;

import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;

public class Client {

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

    public Client(SocketHandler socketHandler) {
        if (socketHandler != null) {
            this.socketHandler = socketHandler;
            this.connected = true;
            this.port = socketHandler.getPort();
        } else {
            this.connected = false;
        }
    }

    public void disconnect() {
        // TODO: handle disconnection
        this.connected = false;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public int getPort() {
        return port;
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
            this.socketHandler.write(message.toString() + "\n");
        }
    }
}
