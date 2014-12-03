package imag.quizz.server.game;

import imag.quizz.common.network.SocketHandler;

/**
 * Represents a Server.
 */
public final class Server extends Client {

    /**
     * The Server's ID.
     */
    private int id;

    /**
     * The Server's IP / FQDN.
     */
    private String ip;

    public Server(SocketHandler socketHandler, int id) {
        super(socketHandler);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
