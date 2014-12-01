package imag.quizz.common.network;

import imag.quizz.common.tool.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SocketSender extends AbstractRepeatingThread {

    private final BufferedWriter writer;
    private final Deque<String>  buffer;

    /* package */ SocketSender(final BufferedWriter writer, final MessageHandler handler) {
        super(" S-Sender ", 50);
        this.writer = writer;
        this.buffer = new ConcurrentLinkedDeque<>();
        handler.registerSocketSender(this);
    }

    @Override
    public void work() throws InterruptedException {
        String mes;
        try {
            while ((mes = this.buffer.poll()) != null) {
                this.writer.write(mes);
            }
            this.writer.flush();
        } catch (final IOException e) {
            Log.error("Failed to write to socket", e);
        }
    }

    public void write(final String message) {
        this.buffer.offer(message);
    }

    public void writeFirst(final String message) {
        this.buffer.offerFirst(message);
    }

    /* package */ boolean hasAnythingToWrite() {
        return !this.buffer.isEmpty();
    }

    /* package */ void kill() {
        try {
            this.writer.close();
        } catch (final IOException e) {
            Log.warn("Failed to close socket writer (is it already closed?)", e);
        }
    }
}
