package imag.quizz.common.protocol;

import imag.quizz.common.Controller;
import imag.quizz.common.network.AbstractRepeatingThread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PingPongTask extends AbstractRepeatingThread {

    private static final int PING_FREQ = 10_000;

    private final int timeout;

    private final Controller controller;

    // Uris from which we're waiting a pong
    private final Set<String> waitingPong;

    // Uri ; Date of last Ping sent
    private final Map<String, Long> lastPingDate;

    // Uris
    private final Set<String> pingedPorts;

    public PingPongTask(final Controller controller, final int timeout) {
        super("PingPongTask", 250);
        this.controller = controller;
        this.timeout = timeout;
        this.waitingPong = new CopyOnWriteArraySet<>();
        this.lastPingDate = new ConcurrentHashMap<>();
        this.pingedPorts = new CopyOnWriteArraySet<>();
    }

    public boolean addUri(final String uri) {
        this.lastPingDate.put(uri, 0L);
        return this.pingedPorts.add(uri);
    }

    public boolean removeUri(final String uri) {
        this.waitingPong.remove(uri);
        this.lastPingDate.remove(uri);
        return this.pingedPorts.remove(uri);
    }

    public void pong(final String uri) {
        if (this.waitingPong.contains(uri)) {
            this.waitingPong.remove(uri);
        }
    }

    @Override
    protected void work() throws InterruptedException {
        final long currentTime = System.currentTimeMillis();
        for (final String uri : this.pingedPorts) {
            if (this.waitingPong.contains(uri)) {
                if (this.lastPingDate.get(uri) + this.timeout < currentTime) {
                    // we've been waiting too long for a pong
                    this.controller.pingTimeout(uri);
                    this.waitingPong.remove(uri);
                }
            } else if (this.lastPingDate.get(uri) + PingPongTask.PING_FREQ < currentTime) {
                this.controller.ping(uri);
                this.lastPingDate.put(uri, currentTime);
                this.waitingPong.add(uri);
            }
        }
    }
}
