package imag.quizz.common.network;

import imag.quizz.common.tool.Log;

import java.io.*;
import java.net.Socket;

public class SocketHandler {

    private final Socket         socket;
    private final SocketSender   socketSender;
    private final SocketReceiver socketReceiver;
    private final MessageHandler handler;

    public SocketHandler(final Socket socket, final MessageHandler handler) throws IOException {
        this.handler = handler;
        this.socket = socket;

        // Prevent infinite condition on reader.readLine() in SocketReceiver
        this.socket.setSoTimeout(50);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));

        final int port = this.socket.getPort();

        this.socketSender = new SocketSender(writer);
        handler.registerSocketHandler(port, this);
        this.socketReceiver = new SocketReceiver(reader, this, port);

        this.socketSender.start();
        this.socketReceiver.start();
    }

    /* package */ MessageHandler getHandler() {
        return this.handler;
    }

    public boolean hasAnythingToWrite() {
        return this.socketSender.hasAnythingToWrite();
    }

    public void write(final String message) {
        this.socketSender.write(message);
    }

    public boolean isReady() {
        return !this.socket.isClosed();
    }

    public boolean isStopped() {
        return this.socketSender.isInterrupted() && this.socketReceiver.isInterrupted();
    }

    public void kill() {
        this.socketReceiver.askStop();
        this.socketSender.askStop();
        try {
            this.socketReceiver.join();
        } catch (final InterruptedException e) {
            Log.warn("SocketHandler interrupted!", e);
        }

        try {
            this.socketSender.join();
        } catch (final InterruptedException e) {
            Log.warn("SocketHandler interrupted!", e);
        }

        try {
            this.socket.close();
        } catch (final IOException e) {
            Log.warn("Failed to close socket (is it already closed?)", e);
        }
    }
}
