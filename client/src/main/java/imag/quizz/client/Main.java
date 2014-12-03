package imag.quizz.client;

import imag.quizz.client.game.Manager;
import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Quizz Client entry point.
 */
public final class Main {

    /**
     * Quizz Client entry point.
     */
    public static void main(final String[] args) {
        new Main();
    }

    private static final Random RANDOM = new Random();

    private Config config;

    private Main() {
        Log.info("Starting...");

        try {
            this.config = new Config();
        } catch (final IOException e) {
            Log.fatal("Failed to parse configuration", e);
            System.exit(1);
        }

        final MessageHandler msgHandler = new ClientMessageHandler();
        msgHandler.start();

        final List<Integer> serverInfos = new LinkedList<>();
        for (final int i : this.config.getServers().keySet()) {
            serverInfos.add(i);
        }

        Collections.shuffle(serverInfos);

        SocketHandler handler = null;
        for (final int id : serverInfos) {
            final ServerInfo info = this.config.getServers().get(id);
            try {
                handler = new SocketHandler(new Socket(info.getHost(), info.getPlayerPort()), msgHandler);
                break;
            } catch (final IOException ignored) {
            }
        }
        if (handler == null) {
            Log.fatal("No server available");
            System.exit(2); // TODO Be less violent
            return;
        }

        // Create main window
        new Window(new Manager());

        Log.info("Ready");
    }

    public Config getConfig() {
        return this.config;
    }
}
