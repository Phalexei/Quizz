package imag.quizz.client;

import imag.quizz.client.game.Manager;
import imag.quizz.client.network.ConnectionManager;
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

        // Create main window
        new Window(new Manager(config));

        Log.info("Ready");
    }

    public Config getConfig() {
        return this.config;
    }
}
