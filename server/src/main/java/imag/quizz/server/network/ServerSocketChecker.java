package imag.quizz.server.network;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Created by Ribesg.
 */
public class ServerSocketChecker extends Thread {

    private final MessageHandler              messageHandler;
    private final Map<Integer, SocketHandler> handlers;
    private final int                         serverSocketPort;
    private final boolean                     listensToClients;

    public ServerSocketChecker(final MessageHandler messageHandler, final Map<Integer, SocketHandler> handlers, final int serverSocketPort, final boolean listensToClients) {
        super((listensToClients ? "Client" : "Server") + "ServerSocketChecker");
        this.messageHandler = messageHandler;
        this.handlers = handlers;
        this.serverSocketPort = serverSocketPort;
        this.listensToClients = listensToClients;
    }

    @Override
    public void run() {
        final ServerSocket server;
        try {
            server = new ServerSocket(this.serverSocketPort);
        } catch (final IOException e) {
            Log.error("Failed to create Server socket", e);
            return;
        }

        Socket inSocket;
        while (!this.isInterrupted()) {
            try {
                inSocket = server.accept();
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("New " + (this.listensToClients ? "Client" : "Server") + " on Port : " + inSocket.getPort());
                }
                try {
                    this.handlers.put(inSocket.getPort(), new SocketHandler(inSocket, this.messageHandler));
                } catch (final IOException e) {
                    Log.error("Failed to create socket handler", e);
                    try {
                        inSocket.close();
                    } catch (final IOException ignored) {
                    }
                }
            } catch (final IOException e) {
                Log.error("Failed to open accept socket", e);
            }
        }
    }
}
