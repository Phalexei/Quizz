package imag.quizz.server.game;

/**
 * Represents a Server.
 */
public final class Server extends Peer {

    private boolean isLeader;

    public Server(final int id, final int port) {
        super(id, port);
    }

    public boolean isLeader() {
        return this.isLeader;
    }

    public void setLeader(final boolean isLeader) {
        this.isLeader = isLeader;
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }
}
