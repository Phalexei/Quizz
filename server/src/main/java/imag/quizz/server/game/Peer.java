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
     * The peer current uri
     */
    protected String uri;

    public Peer(final long id, final String uri) {
        this.id = id;
        this.uri = uri;
    }

    public long getId() {
        return this.id;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public abstract Type getType();
}
