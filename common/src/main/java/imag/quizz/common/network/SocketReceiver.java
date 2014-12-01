package imag.quizz.common.network;

import imag.quizz.common.tool.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketReceiver extends AbstractRepeatingThread {

    private final BufferedReader reader;
    private final MessageHandler handler;
    private final int port;

    /* package */ SocketReceiver(final BufferedReader reader, final MessageHandler handler, int port) {
        super("S-Receiver", 10);
        this.reader = reader;
        this.handler = handler;
        this.port = port;
    }

    @Override
    public void work() {
        String mes;
        try {
            while ((mes = this.reader.readLine()) != null) {
                this.handler.addMessage(port, mes);
            }
        } catch (final SocketTimeoutException ignored) {
            // readLine() Timeout)
        } catch (final SocketException e) {
            Log.error("Socket broken, interrupting", e);
            this.interrupt();
            // TODO stop handling what we're connected to (Server/Client) and do appropriate things
        } catch (final IOException e) {
            Log.error("Failed to read from socket", e);
        }
    }

    /* package */ void kill() {
        try {
            this.reader.close();
        } catch (final IOException e) {
            Log.warn("Failed to close socket reader (is it already closed?)", e);
        }
    }
}
