package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;

/**
 * Represents a server, with its state and connections
 */
public class Server {
    private final int serverId;

    public Server(int serverId) {
        this.serverId = serverId;

        final MessageHandler handler = new ServerMessageHandler();
        handler.start();

        Config config = new Config();
        //TODO: sanitize
        int port = Integer.parseInt(config.getServers().get(serverId).split(":")[1]);

        final ConnectionManager coMgr = new ConnectionManager(port, handler, config, serverId);
        coMgr.start();
    }
}
