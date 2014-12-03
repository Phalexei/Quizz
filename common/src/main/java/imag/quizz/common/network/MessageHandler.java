package imag.quizz.common.network;

import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.Pair;
import org.apache.log4j.Level;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue of incoming messages and handling thread.
 * Implementing classes should only override
 * {@link #handleMessage(int, imag.quizz.common.protocol.message.Message)}
 */
public abstract class MessageHandler extends AbstractRepeatingThread {

    /**
     * Received messages ready to be handled.
     */
    private final ConcurrentLinkedQueue<Pair<Integer, String>> messagePool = new ConcurrentLinkedQueue<>();

    /**
     * Map of opened sockets.
     */
    private ConcurrentHashMap<Integer, SocketHandler> handlers = new ConcurrentHashMap<>();

    protected MessageHandler(final String name) {
        super(name, 50);
    }

    /**
     * Adds a message to the queue. It will be handled in
     * {@link #handleMessage(int, imag.quizz.common.protocol.message.Message)}
     * @param port the port on which to send the message
     * @param message the message to add to the queue
     */
    public final void addMessage(final int port, final String message) {
        messagePool.offer(new Pair<>(port, message));
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Message queued : " + message);
        }
    }

    /**
     * Registers the {@link imag.quizz.common.network.SocketSender} as the correct
     * sender on the specified port.
     * @param port the port on which to bind the sender
     * @param socketHandler the handler to bind on the port
     */
    public final void registerSocketHandler(final int port, final SocketHandler socketHandler) {
        this.handlers.put(port, socketHandler);
    }

    /**
     * Sends a message to the socket attached to this handler on this port
     * @param port the port on which to send the message
     * @param message the message to be sent
     */
    public final void send(final int port, final Message message) {
        this.handlers.get(port).write(message.toString() + '\n');
    }

    @Override
    public final void work() {
        String messageString;
        Pair<Integer, String> portAndMessage;
        while (!messagePool.isEmpty()) {
            portAndMessage = messagePool.poll();
            if (portAndMessage != null) {
                messageString = portAndMessage.getB();
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("Message handled : " + messageString);
                }
                try {
                    final Message message = Message.fromString(messageString);
                    handleMessage(portAndMessage.getA(), message);
                } catch (final IllegalArgumentException e) {
                    Log.error("Received invalid message, ignoring it", e);
                }
            }
        }
    }

    /**
     * This method is called by a {@link imag.quizz.common.network.SocketReceiver}
     * on each incoming message. The treatment of the message should happen here.
     * @param port the message's origin port
     * @param message the message to be handled
     */
    public abstract void handleMessage(int port, Message message);
}
