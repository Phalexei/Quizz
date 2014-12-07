package imag.quizz.client.ui;

import imag.quizz.client.game.ClientController;
import imag.quizz.client.ui.panel.*;
import imag.quizz.client.ui.panel.Panel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;

public class Window {

    private static final int WINDOW_WIDTH   = 800;
    private static final int WINDOW_HEIGHT  = 600;
    private static final int LOG_KEEP_LINES = 1000;
    private final JTextArea logsTextArea;

    final JPanel mainPanel;
    final JFrame frame;

    private final ChoicePanel       choicePanel;
    private final GamesPanel        gamesPanel;
    private final LoginPanel        loginPanel;
    private final NoConnectionPanel noConnectionPanel;
    private final WelcomePanel      welcomePanel;
    private final NewGamePanel      newGamePanel;
    private       PanelType         currentPanelType;
    private       Panel             currentPanel;

    private final ClientController clientController;

    public enum PanelType {
        CHOICE,
        GAMES,
        LOGIN,
        NO_CONNECTION,
        WELCOME,
        NEW_GAME
    }

    public Window(final ClientController clientController) {
        this.clientController = clientController;

        this.welcomePanel = new WelcomePanel();
        this.noConnectionPanel = new NoConnectionPanel(clientController);
        this.loginPanel = new LoginPanel(clientController);
        this.gamesPanel = new GamesPanel(clientController);
        this.newGamePanel = new NewGamePanel(clientController);
        this.choicePanel = new ChoicePanel(clientController);

        this.logsTextArea = new JTextArea();
        this.logsTextArea.setEditable(false);
        final JScrollPane logsScrollPane = new JScrollPane(this.logsTextArea);
        logsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 1));
        rightPanel.add(logsScrollPane);

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridLayout(1, 2));
        this.mainPanel.add(this.welcomePanel);
        this.currentPanel = this.welcomePanel;
        this.mainPanel.add(rightPanel);

        this.frame = new JFrame("Quizz");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setContentPane(this.mainPanel);

        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setResizable(true);
        this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        new Log4JAppender(this);

        this.clientController.setWindow(this);
        this.clientController.start();
        this.clientController.connect();
    }

    public void setPanel(final PanelType panel) {
        if (panel != this.currentPanelType && this.currentPanel.isReady()) {
            Panel newPanel = null;
            switch (panel) {
                case WELCOME:
                    newPanel = this.welcomePanel;
                    break;
                case CHOICE:
                    newPanel = this.choicePanel;
                    break;
                case GAMES:
                    newPanel = this.gamesPanel;
                    break;
                case LOGIN:
                    newPanel = this.loginPanel;
                    break;
                case NO_CONNECTION:
                    newPanel = this.noConnectionPanel;
                    break;
                case NEW_GAME:
                    newPanel = this.newGamePanel;
                    break;
            }

            if (newPanel != null && !newPanel.equals(this.mainPanel.getComponent(0))) {
                this.currentPanelType = panel;
                this.currentPanel = newPanel;
                this.mainPanel.remove(0);
                this.mainPanel.add(newPanel, 0);
                final int width = this.frame.getWidth();
                final int height = this.frame.getHeight();
                this.frame.pack();
                this.frame.setSize(width, height);
            }
        }
    }

    public void showError(String error) {
        this.currentPanel.showError(error);
    }

    public void clearGames() {
        this.gamesPanel.clearGames();
    }

    public void addGame(final long gameId, final boolean wait, int myScore, final int myCurrentQuestion, final String opponent, final int oppScore, final int oppCurrentQuestion) {
        this.gamesPanel.addGame(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion);
    }

    public void setQuestion(final String question) {
        this.choicePanel.setQuestion(question);
    }

    public void setAnswer(final int num, final String answer) {
        this.choicePanel.setAnswer(num, answer);
    }

    public void questionTimeout() {
        this.choicePanel.questionTimeout();
    }

    public void lockButtons() {
        this.choicePanel.toggleButtons(false);
    }

    public void unlockButtons() {
        this.choicePanel.toggleButtons(true);
    }

    public void log(final String line) {
        this.logsTextArea.append(line);
        final Document document = Window.this.logsTextArea.getDocument();
        final Element root = document.getDefaultRootElement();
        if (root.getElementCount() > LOG_KEEP_LINES) {
            final Element first = root.getElement(0);
            try {
                document.remove(first.getStartOffset(), first.getEndOffset());
            } catch (final BadLocationException ignored) {
                // Cannot happen
            }
        }
        Window.this.logsTextArea.setCaretPosition(document.getLength());
    }

    public void noConnection() {
        this.setPanel(PanelType.NO_CONNECTION);
    }

    public void connected() {
        if (this.clientController.isLoggedIn()) {
            this.setPanel(PanelType.GAMES);
        } else {
            this.setPanel(PanelType.LOGIN);
        }
    }

    public void loggedIn() {
        this.setPanel(PanelType.GAMES);
    }
}
