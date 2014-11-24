package imag.quizz.server;

import imag.quizz.common.network.ConnectionManager;
import imag.quizz.common.network.MessageHandler;

/**
 *
 */
public class Main {

    private static final int SERVER_PORT = 26001;
    /**
     *
     */
    public static void main(String[] args) {

        final MessageHandler handler = new MessageHandler();
        handler.start();

        final ConnectionManager coMgr = new ConnectionManager(SERVER_PORT, handler);
        coMgr.start();

        while(true);
    }
}
