package imag.quizz.server.network;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ribesg.
 */
public class ServerSocketChecker extends Thread {

    private final int                     serverSocketPort;
    private final boolean listensToPlayers;
    private final ServerConnectionManager serverConnectionManager;
    private final MessageHandler          messageHandler;

    public ServerSocketChecker(ServerConnectionManager serverConnectionManager, final int serverSocketPort, final boolean listensToPlayers, MessageHandler messageHandler) {
        super((listensToPlayers ? "Player" : "Server") + "ServerSocketChecker");
        this.serverSocketPort = serverSocketPort;
        this.listensToPlayers = listensToPlayers;
        this.serverConnectionManager = serverConnectionManager;
        this.messageHandler = messageHandler;
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
                    Log.debug("New " + (this.listensToPlayers ? "Player" : "Server") + " on Port : " + inSocket.getPort());
                }
                try {
                    SocketHandler socketHandler = new SocketHandler(inSocket, this.messageHandler);
                    serverConnectionManager.addNewConnection(listensToPlayers, socketHandler, inSocket.getPort());
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
