package imag.quizz.server.network;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.tool.SockUri;
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

    protected final Map<String, Peer>          connectedPeers;
    protected final Map<String, SocketHandler> connections;
    protected final MessageHandler             messageHandler;
    protected final ServerSocketChecker        serverSocketChecker;
    protected final long                       ownId;

    protected ConnectionManager(final MessageHandler messageHandler, final boolean isPlayer, final int localPort, final long ownId) {
        this.connectedPeers = new HashMap<>();
        this.connections = new HashMap<>();
        this.messageHandler = messageHandler;
        this.ownId = ownId;
        this.serverSocketChecker = new ServerSocketChecker(isPlayer, this, localPort);

        this.serverSocketChecker.start();
    }

    /**
     * Checks if the connection for given uri, if any, is a connection
     * to a known peer.
     *
     * @param uri the uri
     *
     * @return true if the connection exists and links to a peer, false
     * otherwise
     */
    public boolean linksToPeer(final String uri) {
        return this.connectedPeers.containsKey(uri);
    }

    /**
     * Gets the Peer associated to an uri.
     *
     * @param uri the uri
     *
     * @return the peer if any
     */
    public Peer getLinkedPeer(final String uri) {
        return this.connectedPeers.get(uri);
    }

    /**
     * Sends a message to a Peer.
     *
     * @param peer the peer
     * @param message the message
     */
    public void send(final Peer peer, final Message message) {
        Validate.isTrue(this.connectedPeers.containsValue(peer), "Invalid Peer!");
        this.send(peer.getUri(), message);
    }

    /**
     * Sends a message to the Peer connected with the given uri.
     *
     * @param uri the uri
     * @param message the message
     */
    public void send(final String uri, final Message message) {
        Validate.isTrue(this.connections.containsKey(uri), "Invalid port!");
        this.connections.get(uri).write(message.toString());
    }

    /**
     * Registers a new incoming connection.
     *
     * @param socket the socket
     */
    public void newIncomingConnection(final Socket socket) throws IOException {
        this.connections.put(SockUri.from(socket), new SocketHandler(socket, this.messageHandler));
    }

    /**
     * Registers a new connection.
     */
    public void newConnection(final Peer peer, final Socket socket) throws IOException {
        final String uri = SockUri.from(socket);
        this.connections.put(uri, new SocketHandler(socket, this.messageHandler));
        this.connectedPeers.put(uri, peer);
    }

    public void learnConnectionPeerIdentity(final Peer peer, final SocketHandler socketHandler) {
        final Socket socket = socketHandler.getSocket();
        final String uri = SockUri.from(socket);
        final Peer oldPeer = this.connectedPeers.get(uri);
        if (oldPeer != null) {
            oldPeer.setUri(null);
        }
        this.connectedPeers.put(uri, peer);
        peer.setUri(uri);
    }

    public void forgetConnection(final String uri) {
        this.connectedPeers.remove(uri);
        this.connections.remove(uri);
    }

    public void killConnection(final String uri) {
        final SocketHandler handler = this.connections.get(uri);
        if (handler != null) {
            handler.kill();
        }
    }

    public long getOwnId() {
        return this.ownId;
    }
}
