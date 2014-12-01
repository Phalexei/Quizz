package imag.quizz.common.network;

import imag.quizz.common.tool.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SocketSender extends AbstractRepeatingThread {

    private final BufferedWriter writer;
    private final Deque<String>  buffer;

    /* package */ SocketSender(final BufferedWriter writer) {
        super(" S-Sender ", 50);
        this.writer = writer;
        this.buffer = new ConcurrentLinkedDeque<>();
    }

    @Override
    protected void work() {
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

    /* package */ void write(final String message) {
        this.buffer.offer(message);
    }

    /* package */ boolean hasAnythingToWrite() {
        return !this.buffer.isEmpty();
    }
}
