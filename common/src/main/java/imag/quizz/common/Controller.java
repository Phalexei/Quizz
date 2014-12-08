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
     * Send a {@link imag.quizz.common.protocol.message.PingMessage} to the Peer
     * @param uri the uri to the Peer
     */
    public void ping(final String uri);
}
