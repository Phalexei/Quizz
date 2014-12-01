package imag.quizz.common.network;

import imag.quizz.common.protocol.message.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue of incoming messages and handling thread.
 * Implementing classes should only override
 * {@link #handleMessage(imag.quizz.common.protocol.message.Message)}
 */
public abstract class MessageHandler extends Thread {

    private final ConcurrentLinkedQueue<String> messagePool = new ConcurrentLinkedQueue <>();

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
        messagePool.offer(message);
        System.out.println("Message queued : " + message);
    }

    public final void registerSocketSender(SocketSender socketSender) {
        this.socketSender = socketSender;
    }

    /**
     * Sends a message to the socket attached to this handler.
     * @param message : : the message to be sent
     */
    public final void send(Message message) {
        if (socketSender != null) {
            socketSender.write(message.toString() + "\n");
        }
    }

    @Override
    public final void run() {
        String s;
        while (!this.isInterrupted()) {
            s = messagePool.poll();
            if (s != null && s.length() > 0) {
                System.out.println("Message handled : " + s);
                Message message = Message.fromString(s);
                handleMessage(message);
            } else {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
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
