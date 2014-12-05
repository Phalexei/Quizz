package imag.quizz.common.protocol;

import imag.quizz.common.Config;
import imag.quizz.common.Controller;
import imag.quizz.common.network.AbstractRepeatingThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PingPongTask extends AbstractRepeatingThread {

    private static final int PING_FREQ = 10_000;

    private final int timeout;

    private final Controller controller;

    // Ports from which we're waiting a pong
    private final Set<Integer> waitingPong;

    // Port ; Date of last Ping sent
    private final Map<Integer, Long> lastPingDate;

    // Ports
    private final Set<Integer> pingedPorts;

    public PingPongTask(final Controller controller, final int timeout) {
        super("PingPongTask", 250);
        this.controller = controller;
        this.timeout = timeout;
        this.waitingPong = new CopyOnWriteArraySet<>();
        this.lastPingDate = new ConcurrentHashMap<>();
        this.pingedPorts = new CopyOnWriteArraySet<>();
    }

    public boolean addPort(final int port) {
        this.lastPingDate.put(port, 0L);
        return this.pingedPorts.add(port);
    }

    public boolean removePort(final int port) {
        this.waitingPong.remove(port);
        this.lastPingDate.remove(port);
        return this.pingedPorts.remove(port);
    }

    public void pong(final int port) {
        if (this.waitingPong.contains(port)) {
            this.waitingPong.remove(port);
        }
    }

    @Override
    protected void work() throws InterruptedException {
        final long currentTime = System.currentTimeMillis();
        for (final Integer port : this.pingedPorts) {
            if (this.waitingPong.contains(port)) {
                if (this.lastPingDate.get(port) + this.timeout < currentTime) {
                    // we've been waiting too long for a pong
                    this.controller.pingTimeout(port);
                    this.waitingPong.remove(port);
                }
            } else if (this.lastPingDate.get(port) + PingPongTask.PING_FREQ < currentTime){
                this.controller.ping(port);
                this.lastPingDate.put(port, currentTime);
                this.waitingPong.add(port);
            }
        }
    }
}
