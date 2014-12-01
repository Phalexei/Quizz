package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;

import java.io.IOException;

/**
 *
 */
public class Main {

    public static final int SERVER_PORT = 26000;

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

        Config config = null;
        try {
            config = new Config();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(config.getServers().get(serverId).split(":")[1]);

        final ServerConnectionManager coMgr = new ServerConnectionManager(port, handler, config, serverId);

        while (true) {}
    }
}
