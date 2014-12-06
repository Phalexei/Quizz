package imag.quizz.common;

/**
 * Common methods to be implemented by both controllers.
 */
public interface Controller {

    /**
     * Used by {@link imag.quizz.common.protocol.PingPongTask} when a Peer times out.
     *
     * @param port the local port the Peer was connected to
     */
    public void pingTimeout(final int port);

    /**
     * TODO
     * @param port
     */
    public void ping(final int port);
}