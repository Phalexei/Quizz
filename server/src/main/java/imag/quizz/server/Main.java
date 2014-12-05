package imag.quizz.server;

import imag.quizz.common.Config;
import imag.quizz.common.Config.ServerInfo;
import imag.quizz.common.tool.Log;
import imag.quizz.server.game.QuestionBase;

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

    private Integer      ownId;
    private Config       config;
    private QuestionBase questionBase;

    private ServerController serverController;
    private PlayerController playerController;

    private Main(final String[] args) {
        // Handle program arguments
        this.parseArgs(args);

        // Read configuration file and check own configuration presence
        this.loadConfiguration();
        this.checkOwnConfiguration();

        // Read question base file and load all themes and associated questions and answers
        this.loadQuestionBase();

        // Start Server-Server connections management and try to find other servers
        this.initializeServerConnection();

        // Start Player-Server connections management
        //this.initializePlayerConnection();
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
                        this.printUsage();
                        System.exit(4);
                        break;
                }
            }
            if (this.ownId == null) {
                throw new IllegalStateException("Missing ID argument");
            }
            Log.info("Starting server with id=" + this.ownId);
        } catch (final IllegalStateException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            this.printUsage();
            Log.debug("Error was:", e);
            System.exit(5);
        }
    }

    private void loadConfiguration() {
        try {
            this.config = new Config();
        } catch (final IOException e) {
            Log.fatal("Failed to parse configuration", e);
            System.exit(1);
        }
    }

    private void checkOwnConfiguration() {
        final ServerInfo info = this.config.getServers().get(this.ownId);
        if (info == null) {
            Log.fatal("Configuration misses current server (" + this.ownId + ") details");
            System.exit(2);
        }
    }

    private void loadQuestionBase() {
        try {
            this.questionBase = new QuestionBase();
        } catch (final IOException e) {
            Log.fatal("Failed to parse question base", e);
            System.exit(3);
        }
    }

    private void initializeServerConnection() {
        this.serverController = new ServerController(this.ownId, this.config);
        this.serverController.start();
        // TODO Maybe something? Maybe not...
    }

    private void initializePlayerConnection() {
        final int playerPort = this.config.getServers().get(this.ownId).getPlayerPort();
        this.playerController = new PlayerController(this.serverController, playerPort, this.questionBase);
        this.playerController.start();
        // TODO Maybe something? Maybe not...
    }

    private void printUsage() {
        Log.info("Usage: TODO"); // TODO
    }
}
