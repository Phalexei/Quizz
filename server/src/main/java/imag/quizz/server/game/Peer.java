package imag.quizz.server.game;

/**
 * A known peer, client or server.
 */
public abstract class Peer {

    public enum Type {
        SERVER,
        PLAYER,
    }

    /**
     * The peer identifier
     */
    protected final long id;

    /**
     * The peer current port
     */
    protected int port;

    public Peer(final long id, final int port) {
        this.id = id;
        this.port = port;
    }

    public long getId() {
        return this.id;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public abstract Type getType();
}
