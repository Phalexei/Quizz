package imag.quizz.client;

import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.tool.Log;

import java.io.IOException;

/**
 *
 */
public final class Main {

    /**
     *
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
        final SocketHandler handler = new SocketHandler("127.0.0.1", 26001, msgHandler);
        try {
            handler.connect();
        } catch (final IOException e) {
            Log.fatal("Failed to connect to server", e);
            System.exit(2);
        }

        // Create main window
        final Window window = new Window(handler);

        Log.info("Ready");
    }

    public Config getConfig() {
        return this.config;
    }
}
