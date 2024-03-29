package imag.quizz.common.network;

import imag.quizz.common.tool.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketReceiver extends AbstractRepeatingThread {

    private final BufferedReader reader;
    private final SocketHandler  handler;

    /* package */ SocketReceiver(final BufferedReader reader, final SocketHandler handler) {
        super("SocketReceiver", 10);
        this.reader = reader;
        this.handler = handler;
    }

    @Override
    protected void work() {
        String mes;
        try {
            while ((mes = this.reader.readLine()) != null) {
                this.handler.getHandler().queue(this.handler, mes);
            }
        } catch (final SocketTimeoutException ignored) {
            // readLine() Timeout)
        } catch (final SocketException e) {
            Log.debug("Socket broken, interrupting", e);
            this.handler.kill();
        } catch (final IOException e) {
            Log.error("Failed to read from socket", e);
        }
    }
}
