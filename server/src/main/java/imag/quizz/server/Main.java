package imag.quizz.server;

import imag.quizz.common.network.MessageHandler;

/**
 *
 */
public class Main {

    private static final int SERVER_PORT = 26001;
    public static final boolean DEBUG = true;

    /**
     *
     */
    public static void main(String[] args) {

        final MessageHandler handler = new ServerMessageHandler();
        handler.start();

        final ConnectionManager coMgr = new ConnectionManager(SERVER_PORT, handler);
        coMgr.start();

        while (true) ;
    }
}
