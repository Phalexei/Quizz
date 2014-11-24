package imag.quizz.common.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SocketSender extends AbstractRepeatingThread {

    private final BufferedWriter writer;
    private final Deque<String> buffer;

    /* package */ SocketSender(final BufferedWriter writer) {
        super(" S-Sender ", 50);
        this.writer = writer;
        this.buffer = new ConcurrentLinkedDeque<>();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
