package imag.quizz.common.network;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class ServerSocketHandler {

    private final SocketSender   socketSender;
    private final SocketReceiver socketReceiver;

    public ServerSocketHandler(final Socket client, final MessageHandler handler) throws IOException {
        try {
            client.setSoTimeout(50);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));

            this.socketSender = new SocketSender(writer, handler);
            this.socketReceiver = new SocketReceiver(reader, handler);

            this.socketSender.start();
            this.socketReceiver.start();
        } catch (final IOException e) {
            throw new IOException("Failed to handle server socket", e);
        }
    }

    public void write(final String message) {
        this.socketSender.write(message);
    }
}
