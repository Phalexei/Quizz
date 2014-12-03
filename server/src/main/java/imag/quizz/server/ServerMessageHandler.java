package imag.quizz.server;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.Client;
import imag.quizz.server.network.ServerConnectionManager;
import org.apache.log4j.Level;

public class ServerMessageHandler extends MessageHandler {

    private ServerConnectionManager serverConnectionManager;

    public ServerMessageHandler() {
        super("ServerMessageHandler");
    }

    @Override
    public void handleMessage(final int port, final Message message) {
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Server handling message : " + message.toString() + " from port : " + port);
        }

        Client client = serverConnectionManager.getClientByPort(port);

        if (client == null) {
            message.getSenderId(); // TODO: new client using this id ?
        }

        if (client != null) {
            client.receive(message);
        } else {
            Log.error("Client not found on port " + port + ". Message lost :" + message);
        }
    }

    public void registerConnectionManager(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
    }
}
