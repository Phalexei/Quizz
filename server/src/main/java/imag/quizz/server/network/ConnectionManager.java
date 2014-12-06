package imag.quizz.server.network;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.server.game.Peer;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles connections associated to a {@link java.net.ServerSocket}.
 */
public abstract class ConnectionManager {

    protected final Map<Integer, Peer>          connectedPeers;
    protected final Map<Integer, SocketHandler> connections;
    protected final MessageHandler              messageHandler;
    protected final ServerSocketChecker         serverSocketChecker;
    protected final int                         ownId;

    protected ConnectionManager(final MessageHandler messageHandler, final boolean isPlayer, final int localPort, final int ownId) {
        this.connectedPeers = new HashMap<>();
        this.connections = new HashMap<>();
        this.messageHandler = messageHandler;
        this.ownId = ownId;
        this.serverSocketChecker = new ServerSocketChecker(isPlayer, this, localPort);

        this.serverSocketChecker.start();
    }

    /**
     * Checks if the connection on given port, if any, is a connection
     * to a known peer.
     *
     * @param port the port
     *
     * @return true if the connection exists and links to a peer, false
     * otherwise
     */
    public boolean linksToPeer(final int port) {
        return this.connectedPeers.containsKey(port);
    }

    /**
     * Gets the Peer associated to a local port.
     *
     * @param port the port
     *
     * @return the peer is any
     */
    public Peer getLinkedPeer(final int port) {
        return this.connectedPeers.get(port);
    }

    /**
     * Sends a message to a Peer.
     *
     * @param peer the peer
     * @param message the message
     */
    public void send(final Peer peer, final Message message) {
        Validate.isTrue(this.connectedPeers.containsValue(peer), "Invalid Peer!");
        this.send(peer.getPort(), message);
    }

    /**
     * Sends a message to the Peer connected on the provided port.
     *
     * @param port the port
     * @param message the message
     */
    public void send(final int port, final Message message) {
        Validate.isTrue(this.connections.containsKey(port), "Invalid port!");
        this.connections.get(port).write(message.toString());
    }

    /**
     * Registers a new incoming connection.
     *
     * @param socket the socket
     */
    public void newIncomingConnection(final Socket socket) throws IOException {
        this.connections.put(socket.getLocalPort(), new SocketHandler(socket, this.messageHandler));
    }

    /**
     * Registers a new connection.
     */
    public void newConnection(final Peer peer, final Socket socket) throws IOException {
        this.connections.put(socket.getLocalPort(), new SocketHandler(socket, this.messageHandler));
        this.connectedPeers.put(socket.getLocalPort(), peer);
    }

    public void learnConnectionPeerIdentity(final Peer peer, final SocketHandler socketHandler) {
        this.connectedPeers.put(socketHandler.getSocket().getLocalPort(), peer);
    }

    public void forgetConnection(final int port) {
        this.connectedPeers.remove(port);
        this.connections.remove(port);
    }

    public int getOwnId() {
        return this.ownId;
    }
}
