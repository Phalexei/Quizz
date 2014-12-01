package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.ServerSocketHandler;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handles connections
 */
public class ConnectionManager extends Thread {

    private final int port;
    private final int serverId;
    private boolean stop;
    private final HashMap<Integer, ServerSocketHandler> clients;
    private final ArrayList<ServerSocketHandler> servers;
    private final MessageHandler handler;
    private final Config config;

    public ConnectionManager(int port, final MessageHandler handler, Config config, int serverId) {
        this.port = port;
        stop = false;
        clients = new HashMap<>();
        servers = new ArrayList<>(config.getServers().size());
        this.handler = handler;
        this.config = config;
        this.serverId = serverId;
    }

    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);

            //TODO: connect to other servers in config

            Socket inSocket;

            while (!stop) {
                inSocket = server.accept();

                Iterator<Map.Entry<Integer, String>> i = config.getServers().entrySet().iterator();
                boolean isServer = false;

                while (i.hasNext()) {
                    Map.Entry<Integer, String> e = i.next();
                    if (inSocket.getInetAddress().toString().compareTo(e.getValue()) == 0) {
                        System.out.println("New Server on Port : " + inSocket.getPort());
                        isServer = true;
                        servers.add(e.getKey(), new ServerSocketHandler(inSocket, handler));
                        break;
                    }
                }

                if (!isServer) {
                    if (Main.DEBUG) {
                        System.out.println("New client on Port : " + inSocket.getPort());
                    }
                    clients.put(inSocket.getPort(), new ServerSocketHandler(inSocket, handler));
                }
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
