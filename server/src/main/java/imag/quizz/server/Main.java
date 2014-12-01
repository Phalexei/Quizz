package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.tool.Log;
import imag.quizz.server.network.ServerConnectionManager;

import java.io.IOException;

/**
 * Quizz Server entry point.
 */
public final class Main {

    /**
     * Quizz Server entry point.
     */
    public static void main(final String[] args) {
        new Main(args);
    }

    private int    ownId;
    private Config config;

    private Main(final String[] args) {
        this.parseArgs(args);

        try {
            this.config = new Config();
        } catch (final IOException e) {
            Log.fatal("Failed to parse configuration", e);
            System.exit(1);
        }

        final MessageHandler handler = new ServerMessageHandler();
        handler.start();

        final ServerInfo info = this.config.getServers().get(this.ownId);
        if (info == null) {
            Log.fatal("Configuration misses current server (" + this.ownId + ")details");
            System.exit(2);
        }

        new ServerConnectionManager(handler, this.config, this.ownId);
    }

    private void parseArgs(final String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                final String lowerCasedArg = args[i].toLowerCase();
                switch (lowerCasedArg) {
                    case "--ownid":
                    case "-id":
                        this.ownId = Integer.parseInt(args[++i]);
                        break;
                    default:
                        Log.fatal("Usage: TODO");
                        System.exit(3);
                        break;
                }
            }
        } catch (final ArrayIndexOutOfBoundsException | NumberFormatException e) {
            Log.fatal("Usage: TODO" /* TODO */);
            Log.debug("Error was:", e);
            System.exit(4);
        }
    }
}
