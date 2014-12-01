package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;

/**
 *
 */
public class Main {

    private static final int SERVER_PORT = 26001;
    public static final boolean DEBUG = true; //TODO: get rid of

    /**
     *
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException("Usage : specify server number in argument");
        }

        int serverId = Integer.valueOf(args[0]);
        final MessageHandler handler = new ServerMessageHandler();
        handler.start();

        Config config = new Config();
        final ConnectionManager coMgr = new ConnectionManager(SERVER_PORT, handler, config, serverId);
        coMgr.start();

        while (true) ;
    }
}
