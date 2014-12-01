package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;

import java.io.IOException;

/**
 * Represents a server, with its state and connections
 */
public class Server {
    private final int serverId;

    public Server(int serverId) {
        this.serverId = serverId;

        final MessageHandler handler = new ServerMessageHandler();
        handler.start();

        Config config = null;
        try {
            config = new Config();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: sanitize
        int port = Integer.parseInt(config.getServers().get(serverId).split(":")[1]);

        final ServerConnectionManager coMgr = new ServerConnectionManager(port, handler, config, serverId);
    }
}
