package imag.quizz.server.network;

import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This Thread waits for new connections either from Servers or Players.
 */
public class ServerSocketChecker extends Thread {

    private final boolean           listensToPlayers;
    private final int               serverSocketPort;
    private final ConnectionManager connectionManager;

    private boolean stopAsked;

    public ServerSocketChecker(final boolean listensToPlayers, final ConnectionManager connectionManager, final int serverSocketPort) {
        super((listensToPlayers ? "Play" : "Srv") + "SrvSockCheck"); // Thread name
        this.listensToPlayers = listensToPlayers;
        this.serverSocketPort = serverSocketPort;
        this.connectionManager = connectionManager;
        this.stopAsked = false;
    }

    public void askStop() {
        this.stopAsked = true;
    }

    @Override
    public void run() {
        // Opens Server socket on appropriate port
        final ServerSocket server;
        try {
            server = new ServerSocket(this.serverSocketPort);
        } catch (final IOException e) {
            Log.error("Failed to create Server socket", e);
            return;
        }

        // Wait for incoming connections
        while (!this.stopAsked) {
            try {
                final Socket inSocket = server.accept();
                // Incoming connection!
                if (Log.isEnabledFor(Level.DEBUG)) {
                    Log.debug("New " + (this.listensToPlayers ? "Player" : "Server") + " on Port : " + inSocket.getPort());
                }
                try {
                    // Handle the new socket and register the new connection
                    this.connectionManager.newIncomingConnection(inSocket);
                } catch (final IOException e) {
                    Log.error("Failed to create socket handler", e);
                    try {
                        inSocket.close();
                    } catch (final IOException ignored) {
                    }
                }
            } catch (final IOException e) {
                Log.error("Failed to open server socket", e);
            }
        }
    }
}
