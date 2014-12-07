package imag.quizz.common;

/**
 * Common methods to be implemented by both controllers.
 */
public interface Controller {

    /**
     * Used by {@link imag.quizz.common.protocol.PingPongTask} when a Peer times out.
     *
     * @param uri the uri to the Peer
     */
    public void pingTimeout(final String uri);

    /**
     * TODO
     * @param uri
     */
    public void ping(final String uri);
}
