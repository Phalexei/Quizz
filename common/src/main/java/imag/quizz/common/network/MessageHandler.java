package imag.quizz.common.network;

import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue of incoming messages and handling thread.
 * Implementing classes should only override
 * {@link #handleMessage(imag.quizz.common.protocol.message.Message)}
 */
public abstract class MessageHandler extends Thread {

    private final ConcurrentLinkedQueue<String> messagePool = new ConcurrentLinkedQueue<>();

    private SocketSender socketSender;

    public MessageHandler() {
        socketSender = null;
    }

    /**
     * Adds a message to the queue. It will be handled in
     * {@link #handleMessage(imag.quizz.common.protocol.message.Message)}
     * @param message : the message to add to the queue
     */
    public final void addMessage(final String message) {
        this.messagePool.offer(message);
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Message queued : " + message);
        }
    }

    public final void registerSocketSender(final SocketSender socketSender) {
        this.socketSender = socketSender;
    }

    /**
     * Sends a message to the socket attached to this handler.
     * @param message : : the message to be sent
     */
    public final void send(final Message message) {
        if (this.socketSender != null) {
            this.socketSender.write(message.toString() + "\n");
        }
    }

    @Override
    public final void run() {
        String messageString;
        while (!this.isInterrupted()) {
            messageString = this.messagePool.poll();
            if (messageString != null && messageString.length() > 0) {
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("Message queued : " + messageString);
                }
                try {
                    final Message message = Message.fromString(messageString);
                    handleMessage(message);
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
     * @param message : the message to be handled
     */
    public abstract void handleMessage(Message message);
}
