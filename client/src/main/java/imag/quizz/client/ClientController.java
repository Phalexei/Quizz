package imag.quizz.client;

import imag.quizz.client.network.ConnectionManager;
import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.protocol.message.*;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.SockUri;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ribesg.
 */
public class ClientController extends MessageHandler implements Controller {

    private       Window            window;
    private final ConnectionManager connectionManager;
    private final PingPongTask      pingPongTask;
    private       long              playerId;
    private       long              currentGameId;
    private       String            userLogin, userPassHash;

    public ClientController(final Config config) {
        super("Controller");
        this.window = null;
        this.connectionManager = new ConnectionManager(config, this);
        this.playerId = -1; // -1 is invalid : no ID yet
        this.currentGameId = -1; // -1 is invalid : not yet playing
        this.pingPongTask = new PingPongTask(this, 5_000);
        this.pingPongTask.start();
        this.userPassHash = "";
        this.userLogin = "";
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    @Override
    public void handleMessage(final SocketHandler socketHandler, final Message message) {
        final String uri = SockUri.from(socketHandler.getSocket());

        switch (message.getCommand()) {
            // all valid messages
            case PING:
                this.connectionManager.send(new PongMessage(this.playerId, message));
                break;
            case PONG:
                this.pingPongTask.pong(uri);
                break;
            case GAMES:
                this.updateAvailableGames((GamesMessage) message);
                break;
            case THEMES:
                this.showAvailableThemes((ThemesMessage) message);
                break;
            case QUESTION:
                this.showQuestion((QuestionMessage) message);
                break;
            case NOANSWER:
                this.questionTimeout();
                break;
            case WAIT:
                this.waitForOpponent();
                break;
            case END:
                this.gameEnded();
                break;

            // ack messages
            case OK:
                final OkMessage okMessage = (OkMessage) message;
                switch (okMessage.getSource().getCommand()) {
                    case REGISTER:
                        this.connectionManager.send(new GamesMessage(this.playerId, null));
                    case LOGIN:
                        this.setPlayerId(okMessage.getSource().getSourceId());
                        this.window.loggedIn();
                        this.pingPongTask.addUri(SockUri.from(this.connectionManager.getSocketHandler().getSocket()));
                        break;
                }
                break;
            case NOK:
                this.handleNok((NokMessage) message);
                break;

            // invalid messages
            case INIT:
            case REGISTER:
            case LOGIN:
            case PLAY:
            case DROP:
            case NEW:
            case THEME:
            case GAME:
            case ANSWER:
            default:
                this.connectionManager.send(new NokMessage(this.playerId, "Unhandled message", message));
        }
    }

    private void handleNok(final NokMessage message) {
        this.window.showError(message.getText());
    }

    public void askGames() {
        this.connectionManager.send(new GamesMessage(this.playerId, null));
    }

    private void waitForOpponent() {
        this.askGames();
    }

    private void questionTimeout() {
        this.window.questionTimeout();
    }

    public void answerSelected(final boolean question, final int answer) {
        Log.info("Réponse sélectionnée : \"" + answer + '"');
        this.window.lockButtons();

        if (question) {
            this.connectionManager.send(new AnswerMessage(this.playerId, answer));
        } else { // theme choice
            this.connectionManager.send(new ThemeMessage(this.playerId, answer));
        }
    }

    private void showQuestion(final QuestionMessage message) {
        this.window.setPanel(Window.PanelType.CHOICE);
        this.window.setQuestion(message.getQuestion());
        int i = 1;
        for (final String answer : message.getAnswers()) {
            this.window.setAnswer(i++, answer);
        }
        this.window.unlockButtons();
    }

    private void showAvailableThemes(final ThemesMessage message) {
        this.window.setPanel(Window.PanelType.CHOICE);
        this.window.setTheme();
        int i = 1;
        for (final String theme : message.getThemes()) {
            this.window.setAnswer(i++, theme);
        }
        this.window.unlockButtons();
    }

    /**
     * Asks server for a new game
     * @param opponent the chosen opponent. Empty for a random one
     */
    public void newGame(final String opponent) {
        this.connectionManager.send(new NewMessage(this.playerId, opponent));
    }

    /**
     * Displays the NewGamePanel
     */
    public void newGame() {
        this.window.setPanel(Window.PanelType.NEW_GAME);
    }

    /**
     * Asks server to play a game
     * @param gameId the ID of the game
     */
    public void play(final long gameId) {
        this.connectionManager.send(new PlayMessage(this.playerId, gameId));
    }

    /**
     * Refreshes the games list
     * @param message the GamesMessage containing our games list
     */
    private void updateAvailableGames(final GamesMessage message) {
        final String data = message.getGamesData();
        this.window.clearGames();
        if (!data.equals("null")) {
            for (final String game : data.split(Separator.LEVEL_1)) {
                final String[] split = game.split(Separator.LEVEL_2);

                final long gameId = Long.parseLong(split[0]);
                final String opponent = split[1];
                final boolean wait = Boolean.parseBoolean(split[2]);
                final int myScore = Integer.parseInt(split[3]);
                final int myCurrentQuestion = Integer.parseInt(split[4]);
                final int oppScore = Integer.parseInt(split[5]);
                final int oppCurrentQuestion = Integer.parseInt(split[6]);

                this.window.addGame(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion);
            }
        }
        if (!this.isPlaying()) {
            this.window.setPanel(Window.PanelType.GAMES);
        }
    }

    private void gameEnded() {
        this.askGames();
    }

    @Override
    public void lostConnection(final SocketHandler socketHandler) {
        this.pingPongTask.removeUri(SockUri.from(socketHandler.getSocket()));
        this.connectionManager.lostConnection();
        this.connect();
    }

    @Override
    public void pingTimeout(final String uri) {
        if (this.isLoggedIn()) {
            this.lostConnection(this.connectionManager.getSocketHandler());
        }
    }

    @Override
    public void ping(final String uri) {
        if (this.isLoggedIn()) {
            this.connectionManager.send(new PingMessage(this.playerId));
        }
    }

    public void connect() {
        try {
            this.connectionManager.tryConnect();
            if (!this.userLogin.isEmpty() && !this.userPassHash.isEmpty()) {
                this.login(this.userLogin, this.userPassHash.toCharArray());
            }
            this.window.connected();
        } catch (final ConnectionManager.NoServerException e) {
            this.window.noConnection();
        }
    }

    public void login(final String username, final char[] password) {
        this.userLogin = username;
        this.userPassHash = new String(password);
        this.connectionManager.send(new LoginMessage(this.playerId, this.userLogin, this.userPassHash));
    }

    public void register(final String username, final char[] password) {
        this.userLogin = username;
        this.userPassHash = new String(password);
        this.connectionManager.send(new RegisterMessage(this.playerId, this.userLogin, this.userPassHash));
    }

    public boolean isLoggedIn() {
        return this.playerId != -1;
    }

    public boolean isPlaying() {
        return this.currentGameId != -1;
    }

    private void setPlayerId(final long playerId) {
        this.playerId = playerId;
    }
}
