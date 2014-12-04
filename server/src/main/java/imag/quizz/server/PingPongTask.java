package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.network.AbstractRepeatingThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PingPongTask extends AbstractRepeatingThread {

    private final Controller controller;

    // Port ; Waiting for a Pong from this Peer
    private final Map<Integer, Boolean> waitingPong;

    // Port ; Date of last Ping sent
    private final Map<Integer, Long> lastPingDate;

    // Ports
    private final Set<Integer> pingedPorts;

    public PingPongTask(final Controller controller, final Config config) {
        super("PingPongTask", 250);
        this.controller = controller;
        this.waitingPong = new ConcurrentHashMap<>();
        this.lastPingDate = new ConcurrentHashMap<>();
        this.pingedPorts = new CopyOnWriteArraySet<>();
    }

    public boolean addPort(final int port) {
        return this.pingedPorts.add(port);
    }

    public boolean removePort(final int port) {
        return this.pingedPorts.remove(port);
    }

    @Override
    protected void work() throws InterruptedException {
        final Iterator<Entry<Integer, Boolean>> it = this.waitingPong.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<Integer, Boolean> entry = it.next();
            final int port = entry.getKey();
            if (!this.pingedPorts.contains(entry.getKey())) {
                it.remove();
                this.lastPingDate.remove(port);
            }
            if (entry.getValue()) {
                if (this.lastPingDate.get(port) + 3000 < System.currentTimeMillis()) {
                    this.controller.pingTimeout(port);
                }
            } else if (this.lastPingDate.get(port) + 10000 < System.currentTimeMillis()) {
                this.controller.ping(port);
            }
        }
    }
}
