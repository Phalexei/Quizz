package imag.quizz.common.network;

import java.io.*;
import java.net.Socket;

/**
 * Represents a client
 */
public class ServerSocketHandler {
    private SocketSender socketSender;
    private SocketReceiver socketReceiver;

    public ServerSocketHandler(Socket client, MessageHandler handler) {
        try {
            client.setSoTimeout(50);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));

            this.socketSender = new SocketSender(writer, handler);
            this.socketReceiver = new SocketReceiver(reader, handler);

            this.socketSender.start();
            this.socketReceiver.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(final String message) {
        this.socketSender.write(message);

    }
}
