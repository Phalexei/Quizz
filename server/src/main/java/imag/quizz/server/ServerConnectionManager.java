package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.ServerSocketHandler;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles connections
 */
public class ServerConnectionManager {

    private final int                                   serverId;
    private final HashMap<Integer, ServerSocketHandler> clients;
    private final HashMap<Integer, ServerSocketHandler> servers;
    private final MessageHandler                        handler;
    private final Config                                config;

    private Thread serverSocketChecker;
    private Thread clientSocketChecker;

    public ServerConnectionManager(final int port, final MessageHandler handler, Config config, int serverId) {
        clients = new HashMap<>();
        servers = new HashMap<>();
        this.handler = handler;
        this.config = config;
        this.serverId = serverId;

        this.serverSocketChecker = new Thread() {
            @Override
            public void run() {
                try {
                    final ServerSocket server = new ServerSocket(port);

                    connectServers();

                    Socket inSocket;

                    while (!this.isInterrupted()) {
                        inSocket = server.accept();

                        System.out.println("New Server on Port : " + inSocket.getPort());
                        servers.put(inSocket.getPort(), new ServerSocketHandler(inSocket, handler));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.serverSocketChecker.start();

        this.clientSocketChecker = new Thread() {
            @Override
            public void run() {
                try {
                    final ServerSocket server = new ServerSocket(Main.SERVER_PORT);

                    Socket inSocket;

                    while (!this.isInterrupted()) {
                        inSocket = server.accept();

                        System.out.println("New Client on Port : " + inSocket.getPort());
                        clients.put(inSocket.getPort(), new ServerSocketHandler(inSocket, handler));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.clientSocketChecker.start();
    }

    private void connectServers() {
        for (Map.Entry<Integer, String> entry : config.getServers().entrySet()) {
            if (entry.getKey() == this.serverId) {
                continue;
            }
            try {
                String[] connectionInfo = config.getServers().get(entry.getKey()).split(":");
                Socket s = new Socket();
                s.connect(new InetSocketAddress(connectionInfo[0], Integer.parseInt(connectionInfo[1])));
                servers.put(entry.getKey(), new ServerSocketHandler(s, handler));
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    private void broadcast(String message) {
        for (Map.Entry<Integer, ServerSocketHandler> entry : servers.entrySet()) {
            if (entry.getKey() != this.serverId && entry.getValue().isReady()) {
                entry.getValue().write(message);
            }
        }
    }

    public void stop() {
        this.serverSocketChecker.interrupt();
        this.clientSocketChecker.interrupt();
    }

    /**
     * This method checks if a player is connected to this server
     * @param playerID : the numeric ID of the player
     * @return true if player is connected to this server
     */
    public boolean isConnected(int playerID) {
        throw new NotImplementedException("TODO");
    }

    public int getClientPort(int playerId) {
        throw new NotImplementedException("TODO");
    }
}
