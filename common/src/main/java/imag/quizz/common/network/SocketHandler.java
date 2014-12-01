package imag.quizz.common.network;

import java.io.*;
import java.net.Socket;

public class SocketHandler {

    private final String url;
    private final int    port;

    private Socket         socket;
    private SocketSender   socketSender;
    private SocketReceiver socketReceiver;
    private MessageHandler handler;

    public SocketHandler(final String url, final int port, final MessageHandler handler) {
        this.url = url;
        this.port = port;
        this.handler = handler;
    }

    public void connect() throws IOException {
        this.socket = new Socket(this.url, this.port);

        // Prevent infinite condition on reader.readLine() in SocketReceiver
        this.socket.setSoTimeout(50);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));

        this.socketSender = new SocketSender(writer, handler);
        this.socketReceiver = new SocketReceiver(reader, handler);

        this.socketSender.start();
        this.socketReceiver.start();
    }

    public boolean hasAnythingToWrite() {
        return this.socketSender.hasAnythingToWrite();
    }

    public void write(final String message) {
        this.socketSender.write(message);
    }

    public void askStop() {
        this.socketSender.askStop();
        this.socketReceiver.askStop();
    }

    public boolean isStopped() {
        return this.socketSender.isInterrupted() && this.socketReceiver.isInterrupted();
    }

    public void kill() {
        try {
            this.socketReceiver.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        try {
            this.socketSender.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        try {
            this.socket.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
