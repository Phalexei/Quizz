package imag.quizz.client.ui;

import imag.quizz.client.game.ClientController;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window {

    private static final int WINDOW_WIDTH   = 800;
    private static final int WINDOW_HEIGHT  = 600;
    private static final int LOG_KEEP_LINES = 1000;

    private final JButton   topLeftButton;
    private final JButton   topRightButton;
    private final JButton   bottomLeftButton;
    private final JButton   bottomRightButton;
    private final JTextPane questionTextPane;
    private final JTextArea logsTextArea;

    final JPanel mainPanel;
    final JFrame frame;

    private final JPanel    choicePanel;
    private final JPanel    gamesPanel;
    private final JPanel    infoPanel;
    private final JPanel    loginPanel;
    private final JPanel    noConnectionPanel;
    private final JPanel    welcomePanel;
    private Panel currentPanel;

    public enum Panel {
        CHOICE,
        GAMES,
        INFO,
        LOGIN,
        NOCONNECTION,
        WELCOME
    }

    public Window(final ClientController clientController) {
        this.topLeftButton = new JButton("Oui");
        this.topRightButton = new JButton("Non");
        this.bottomLeftButton = new JButton("Peut-être");
        this.bottomRightButton = new JButton("42");

        final InputHandler inputHandler = new InputHandler(clientController);
        this.topLeftButton.addActionListener(inputHandler);
        this.topRightButton.addActionListener(inputHandler);
        this.bottomLeftButton.addActionListener(inputHandler);
        this.bottomRightButton.addActionListener(inputHandler);

        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 2));
        buttonsPanel.add(this.topLeftButton);
        buttonsPanel.add(this.topRightButton);
        buttonsPanel.add(this.bottomLeftButton);
        buttonsPanel.add(this.bottomRightButton);

        final String question = "Quelle est la réponse à la Grande Question sur la Vie, l'Univers et le Reste ?";
        this.questionTextPane = CenteredTextPaneHandler.create(question);
        this.questionTextPane.setEditable(false);

        this.choicePanel = new JPanel();
        this.choicePanel.setLayout(new GridLayout(2, 1));
        this.choicePanel.add(this.questionTextPane);
        this.choicePanel.add(buttonsPanel);

        this.loginPanel = buildLoginPanel(clientController);

        this.noConnectionPanel = buildNoConnectionPanel(clientController);

        this.gamesPanel = null; // TODO
        this.infoPanel = null; // TODO

        this.welcomePanel = buildWelcomePanel();

        this.logsTextArea = new JTextArea();
        this.logsTextArea.setEditable(false);
        final JScrollPane logsScrollPane = new JScrollPane(this.logsTextArea);
        logsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 1));
        rightPanel.add(logsScrollPane);

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridLayout(1, 2));
        this.mainPanel.add(this.choicePanel);
        this.mainPanel.add(rightPanel);

        frame = new JFrame("Quizz");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(this.mainPanel);

        this.setPanel(Panel.WELCOME);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        new Log4JAppender(this);


        clientController.setWindow(this);
        clientController.start();
        clientController.connect();
    }

    public void setPanel(final Panel panel) {
        if (panel != this.currentPanel) {
            JPanel newPanel = null;
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
                case INFO:
                    newPanel = this.infoPanel;
                    break;
                case LOGIN:
                    newPanel = this.loginPanel;
                    break;
                case NOCONNECTION:
                    newPanel = this.noConnectionPanel;
                    break;
            }

            if (newPanel != null && !newPanel.equals(this.mainPanel.getComponent(0))) {
                this.currentPanel = panel;
                this.mainPanel.remove(0);
                this.mainPanel.add(newPanel, 0);
                frame.pack();
                frame.setVisible(true);
                frame.setResizable(true);
                frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            }
        }
    }

    private JPanel buildNoConnectionPanel(final ClientController clientController) {
        final String noConnection = "Aucun serveur disponible.";
        final JTextPane noConnectionText = CenteredTextPaneHandler.create(noConnection);
        final JButton retryButton = new JButton("Réessayer la connexion");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.connect();
            }
        });

        final JPanel noConnectionPanel = new JPanel();
        noConnectionPanel.setLayout(new GridLayout(2, 1));
        noConnectionPanel.add(noConnectionText);
        noConnectionPanel.add(retryButton);

        return noConnectionPanel;
    }

    private JPanel buildWelcomePanel() {
        final String welcome = "Bienvenue sur <Insert Quizz Name here>"; //TODO
        final JTextPane welcomeText = CenteredTextPaneHandler.create(welcome);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridLayout(1, 1));
        welcomePanel.add(welcomeText);

        return welcomePanel;
    }

    private JPanel buildLoginPanel(final ClientController clientController) {
        final String login = "Veuillez vous identifier.";
        final JTextPane loginText = CenteredTextPaneHandler.create(login);
        final JButton loginButton = new JButton("Connexion");
        final JButton registerButton = new JButton("Inscription");
        final JTextField username = new JTextField("Identifiant");
        final JPasswordField password = new JPasswordField("Mot de passe");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.login(username.getText(), password.getPassword());
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.register(username.getText(), password.getPassword());
            }
        });

        final JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 1));
        loginPanel.add(loginText);

        final JPanel loginInfo = new JPanel();
        loginInfo.setLayout(new GridLayout(1, 2));
        loginInfo.add(username);
        loginInfo.add(password);
        loginPanel.add(loginInfo);

        final JPanel loginButtons = new JPanel();
        loginButtons.setLayout(new GridLayout(1, 2));
        loginButtons.add(loginButton);
        loginButtons.add(registerButton);
        loginPanel.add(loginButtons);

        return loginPanel;
    }

    public void setQuestion(final String question) {
        if (this.currentPanel == Panel.CHOICE) {
            CenteredTextPaneHandler.setText(this.questionTextPane, question);
        }
    }

    public void setAnswer(final int num, final String answer) {
        switch (num) {
            case 1:
                this.topLeftButton.setText(answer);
                break;
            case 2:
                this.topRightButton.setText(answer);
                break;
            case 3:
                this.bottomLeftButton.setText(answer);
                break;
            case 4:
                this.bottomRightButton.setText(answer);
                break;
            default:
                throw new IllegalArgumentException("Invalid number: " + num);
        }
    }

    public void lockButtons() {
        this.topLeftButton.setEnabled(false);
        this.topRightButton.setEnabled(false);
        this.bottomLeftButton.setEnabled(false);
        this.bottomRightButton.setEnabled(false);
    }

    public void unlockButtons() {
        this.topLeftButton.setEnabled(true);
        this.topRightButton.setEnabled(true);
        this.bottomLeftButton.setEnabled(true);
        this.bottomRightButton.setEnabled(true);
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
        this.setPanel(Panel.NOCONNECTION);
    }

    public void connected() {
        this.setPanel(Panel.GAMES);
    }
}
