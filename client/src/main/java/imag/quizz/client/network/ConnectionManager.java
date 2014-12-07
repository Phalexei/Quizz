package imag.quizz.client.network;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by andrepot on 12/5/14.
 */
public class ConnectionManager {

    public class NoServerException extends Exception {
    }

    private final Config         config;
    private       SocketHandler  socketHandler;
    private final MessageHandler messageHandler;

    public ConnectionManager(final Config config, final MessageHandler messageHandler) {
        this.config = config;
        this.messageHandler = messageHandler;
    }

    public void tryConnect() throws NoServerException {
        final List<Long> serverInfos = new LinkedList<>();
        for (final long i : this.config.getServers().keySet()) {
            serverInfos.add(i);
        }

        Collections.shuffle(serverInfos);

        this.socketHandler = null;
        for (final long id : serverInfos) {
            final ServerInfo info = this.config.getServers().get(id);
            try {
                this.socketHandler = new SocketHandler(new Socket(info.getHost(), info.getPlayerPort()), this.messageHandler);
                break;
            } catch (final IOException e) {
                Log.debug("Failed to connect to server " + info.getId() + ", ignoring");
                Log.trace("Error was:", e);
            }
        }

        if (this.socketHandler == null) {
            throw new NoServerException();
        }
    }

    public boolean isConnected() {
        return this.socketHandler != null; //TODO other checks
    }

    public void send(final Message message) {
        if (this.isConnected()) {
            this.socketHandler.write(message.toString());
        }
    }

    public void lostConnection() {
        this.socketHandler = null;
    }

    public SocketHandler getSocketHandler() {
        return this.socketHandler;
    }
}
