package imag.quizz.common.network;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue of incoming messages and handling thread
 */
public class MessageHandler extends Thread {

    private final Queue<String> messagePool = new LinkedList<>();

    public MessageHandler() {

    }

    public void addMessage(final String message) {
        messagePool.offer(message);
        System.out.println("Message queued : " + message);
    }

    @Override
    public void run() {
        String s;
        while (!this.isInterrupted()) {
            s = messagePool.poll();
            if (s != null && s.length() > 0) {
                System.out.println("Message handled : " + s);
            } else {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
