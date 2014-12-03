package imag.quizz.server.game;

import imag.quizz.common.network.AbstractRepeatingThread;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.protocol.message.PingMessage;
import imag.quizz.common.protocol.message.PongMessage;
import imag.quizz.server.network.ServerConnectionManager;

/**
 * Represents a Server.
 */
public final class Server extends Client {

    /**
     * The Server's IP / FQDN.
     */
    private String ip;
    private final AbstractRepeatingThread proc;
    private boolean waitingPong;
    private final static int SERVER_PROC_TIME = 50;
    private final static int SERVER_PONG_TIMEOUT = 3000;
    private int pongCounter;

    public Server(ServerConnectionManager connectionManager, SocketHandler socketHandler, int id) {
        super(connectionManager, socketHandler, id);
        this.proc = new AbstractRepeatingThread("server proc", SERVER_PROC_TIME) {
            @Override
            protected void work() throws InterruptedException {
                if (Server.this.isConnected()) {
                    Server.this.proc();
                }
            }
        };
        this.proc.start();

        this.waitingPong = false;
        this.pongCounter = 0;
    }

    @Override
    protected void disconnected() {
        //TODO: handle disconnection
    }

    private void proc() {
        checkPing();
    }

    private void checkPing() {
        if (this.waitingPong) {
            if ((this.pongCounter += SERVER_PROC_TIME) >= SERVER_PONG_TIMEOUT) {
                //TODO: no pong received
            }
        } else {
            send(new PingMessage(this.getId()));
            this.waitingPong = true;
            this.pongCounter = 0;
        }
    }

    @Override
    public void receive(Message message) {
        switch (message.getCommand()) {
            case PING:
                send(new PongMessage((PingMessage)message));
                break;
            case PONG:
                this.waitingPong = false;
                break;

            case INIT:
                break;
            case GAME:
            case DROP:
            case NOANSWER:
            case ANSWER:
            case THEME:
            case LOGIN:
            case REGISTER:
                if (!connectionManager.isLeader(this.getId())) {
                    // we received a message to broadcast
                    connectionManager.broadcast(message);
                }
                break;
        }
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }
}
