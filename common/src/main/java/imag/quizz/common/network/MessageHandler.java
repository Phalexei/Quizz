package imag.quizz.common.network;

import imag.quizz.common.protocol.Separator;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.Pair;
import org.apache.log4j.Level;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue of incoming messages and handling thread.
 *
 * Implementing classes should only override
 * {@link #handleMessage(imag.quizz.common.network.SocketHandler, imag.quizz.common.protocol.message.Message)}
 */
public abstract class MessageHandler extends AbstractRepeatingThread {

    /**
     * Received messages ready to be handled.
     */
    private final ConcurrentLinkedQueue<Pair<SocketHandler, String>> messagePool = new ConcurrentLinkedQueue<>();

    /**
     * Constructor.
     *
     * @param name name of the Thread
     */
    protected MessageHandler(final String name) {
        super(name, 50);
    }

    /**
     * Adds a message to the queue. It will be handled in
     * {@link #handleMessage(imag.quizz.common.network.SocketHandler, imag.quizz.common.protocol.message.Message)}
     *
     * @param socketHandler the socket handler that received the message
     * @param message the message
     */
    public final void queue(final SocketHandler socketHandler, final String message) {
        this.messagePool.offer(new Pair<>(socketHandler, message));
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Message queued: " + Separator.clean(message));
        }
    }

    @Override
    public final void work() {
        String messageString;
        Pair<SocketHandler, String> socketAndMessage;
        while (!this.messagePool.isEmpty()) {
            socketAndMessage = this.messagePool.poll();
            if (socketAndMessage != null) {
                messageString = socketAndMessage.getB();
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("Handling message: " + Separator.clean(messageString));
                }
                try {
                    final Message message = Message.fromString(messageString);
                    this.handleMessage(socketAndMessage.getA(), message);
                } catch (final IllegalArgumentException e) {
                    Log.error("Received invalid message, ignoring it", e);
                }
            }
        }
    }

    /**
     * This method is called by a {@link imag.quizz.common.network.SocketReceiver}
     * on each incoming message. The treatment of the message should happen here.
     *
     * @param socketHandler the socket handle that received the message
     * @param message the message to be handled
     */
    public abstract void handleMessage(final SocketHandler socketHandler, final Message message);

    public abstract void lostConnection(final SocketHandler socketHandler);
}
