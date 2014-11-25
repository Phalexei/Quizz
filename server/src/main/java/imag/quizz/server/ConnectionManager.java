package imag.quizz.server;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.ServerSocketHandler;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Handles connections from clients
 */
public class ConnectionManager extends Thread {

    private final int port;
    private boolean stop;
    private final HashMap<Integer, ServerSocketHandler> clients;
    private final MessageHandler handler;

    public ConnectionManager(int port, final MessageHandler handler) {
        this.port = port;
        stop = false;
        clients = new HashMap<>();
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);

            Socket client;

            while (!stop) {
                client = server.accept();
                if (Main.DEBUG) {
                    System.out.println("New client on Port : " + client.getPort());
                }
                clients.put(client.getPort(), new ServerSocketHandler(client, handler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if a player is connected to this server
     * @param playerID : the numeric ID of the player
     * @return true if player is connected to this server
     */
    public boolean isConnected(int playerID) {
        throw new NotImplementedException("TODO");
    }
}
