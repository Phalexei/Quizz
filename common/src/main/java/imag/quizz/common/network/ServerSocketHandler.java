package imag.quizz.common.network;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class ServerSocketHandler {
    private       SocketSender   socketSender;
    private       SocketReceiver socketReceiver;
    private final Socket         socket;

    public ServerSocketHandler(Socket socket, MessageHandler handler) throws IOException {
        this.socket = socket;
        try {
            this.socket.setSoTimeout(50);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));

            this.socketSender = new SocketSender(writer, handler, socket.getPort());
            this.socketReceiver = new SocketReceiver(reader, handler, socket.getPort());

            this.socketSender.start();
            this.socketReceiver.start();
        } catch (final IOException e) {
            throw new IOException("Failed to handle server socket", e);
        }
    }

    public void write(final String message) {
        this.socketSender.write(message);
    }

    public boolean isReady() {
        return !this.socket.isClosed();
    }
}
