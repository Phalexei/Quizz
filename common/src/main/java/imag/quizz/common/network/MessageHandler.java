package imag.quizz.common.network;

import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue of incoming messages and handling thread.
 * Implementing classes should only override
 * {@link #handleMessage(int, imag.quizz.common.protocol.message.Message)}
 */
public abstract class MessageHandler extends Thread {

    class IntAndString {
        private final int    theInt;
        private final String theString;

        public IntAndString(int theInt, String theString) {
            this.theInt = theInt;
            this.theString = theString;
        }

        public int getTheInt() {
            return theInt;
        }

        public String getTheString() {
            return theString;
        }
    }

    private final ConcurrentLinkedQueue<IntAndString> messagePool = new ConcurrentLinkedQueue<>();

    private ConcurrentHashMap<Integer, SocketSender> senders = new ConcurrentHashMap<>();

    /**
     * Adds a message to the queue. It will be handled in
     * {@link #handleMessage(int, imag.quizz.common.protocol.message.Message)}
     * @param port the port on which to send the message
     * @param message the message to add to the queue
     */
    public final void addMessage(final int port, final String message) {
        messagePool.offer(new IntAndString(port, message));
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Message queued : " + message);
        }
    }

    /**
     * Registers the {@link imag.quizz.common.network.SocketSender} as the correct
     * sender on the specified port.
     * @param port the port on which to bind the sender
     * @param socketSender the sender to bind on the port
     */
    public final void registerSocketSender(final int port, final SocketSender socketSender) {
        this.senders.put(port, socketSender);
    }

    /**
     * Sends a message to the socket attached to this handler.
     * @param port the port on which to send the message
     * @param message the message to be sent
     */
    public final void send(final int port, final Message message) {
        final SocketSender socketSender = this.senders.get(port);
        if (socketSender != null) {
            socketSender.write(message.toString() + "\n");
        }
    }

    @Override
    public final void run() {
        String messageString;
        IntAndString portAndMessage;
        while (!this.isInterrupted()) {
            portAndMessage = messagePool.poll();
            if (portAndMessage != null) {
                messageString = portAndMessage.getTheString();
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("Message queued : " + messageString);
                }
                try {
                    final Message message = Message.fromString(messageString);
                    handleMessage(portAndMessage.getTheInt(), message);
                } catch (final IllegalArgumentException e) {
                    Log.error("Received invalid message, ignoring it", e);
                }
            } else {
                try {
                    sleep(50);
                } catch (final InterruptedException e) {
                    Log.warn("MessageHandler interrupted!", e);
                    this.interrupt();
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
