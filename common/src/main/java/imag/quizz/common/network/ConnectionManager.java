package imag.quizz.common.network;

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
            final ServerSocket server =  new ServerSocket(port);

            Socket client;

            while (!stop) {
                client = server.accept();
                System.out.println("New client on Port : " + client.getPort());
                clients.put(client.getPort(), new ServerSocketHandler(client, handler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
