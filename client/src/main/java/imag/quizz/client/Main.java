package imag.quizz.client;

import imag.quizz.client.game.Manager;
import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;

import java.io.IOException;
import java.net.Socket;

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
        try {
            new SocketHandler(new Socket("127.0.0.1", 26000), msgHandler);
        } catch (final IOException e) {
            Log.fatal("Failed to connect to server", e);
            System.exit(2);
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
