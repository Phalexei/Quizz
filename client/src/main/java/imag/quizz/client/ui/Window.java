package imag.quizz.client.ui;

import imag.quizz.client.game.ClientController;

import javax.swing.*;
import javax.swing.text.*;
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

    private final JPanel choicePanel;
    private final JPanel gamesPanel;
    private final JPanel infoPanel;
    private final JPanel loginPanel;
    private final JPanel noConnectionPanel;
    private final JPanel welcomePanel;
    private       Panel  currentPanel;

    private final ClientController clientController;

    public enum Panel {
        CHOICE,
        GAMES,
        INFO,
        LOGIN,
        NO_CONNECTION,
        WELCOME
    }

    public Window(final ClientController clientController) {
        this.clientController = clientController;

        this.topLeftButton = new JButton("Oui");
        this.topRightButton = new JButton("Non");
        this.bottomLeftButton = new JButton("Peut-être");
        this.bottomRightButton = new JButton("42");

        final InputHandler inputHandler = new InputHandler(this.clientController);
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

        this.welcomePanel = this.buildWelcomePanel();

        this.noConnectionPanel = this.buildNoConnectionPanel();

        this.loginPanel = this.buildLoginPanel();

        this.gamesPanel = null; // TODO
        this.infoPanel = null; // TODO

        this.choicePanel = new JPanel();
        this.choicePanel.setLayout(new GridLayout(2, 1));
        this.choicePanel.add(this.questionTextPane);
        this.choicePanel.add(buttonsPanel);

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

        this.frame = new JFrame("Quizz");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setContentPane(this.mainPanel);

        this.setPanel(Panel.WELCOME);
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setResizable(true);
        this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        new Log4JAppender(this);

        this.clientController.setWindow(this);
        this.clientController.start();
        this.clientController.connect();
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
                case NO_CONNECTION:
                    newPanel = this.noConnectionPanel;
                    break;
            }

            if (newPanel != null && !newPanel.equals(this.mainPanel.getComponent(0))) {
                this.currentPanel = panel;
                this.mainPanel.remove(0);
                this.mainPanel.add(newPanel, 0);
                final int width = this.frame.getWidth();
                final int height = this.frame.getHeight();
                this.frame.pack();
                this.frame.setSize(width, height);
            }
        }
    }

    private JPanel buildWelcomePanel() {
        final String welcome = "Bienvenue sur Quizz Duel\n\nConnexion en cours..."; // TODO
        final JTextPane welcomeTextPane = CenteredTextPaneHandler.create(welcome);
        welcomeTextPane.setEditable(false);

        final JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridLayout(1, 1));
        welcomePanel.add(welcomeTextPane);

        return welcomePanel;
    }

    private JPanel buildNoConnectionPanel() {
        final String noConnection = "Aucun serveur disponible.";

        final JTextPane noConnectionTextPane = CenteredTextPaneHandler.create(noConnection);
        noConnectionTextPane.setEditable(false);

        final JButton retryButton = new JButton("Réessayer");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                retryButton.setEnabled(false);

                // TODO Don't do this here. Needs to be done by the main thread!
                Window.this.clientController.connect();
            }
        });

        final JPanel noConnectionPanel = new JPanel();
        final BorderLayout layout = new BorderLayout();
        noConnectionPanel.setLayout(layout);
        noConnectionPanel.add(noConnectionTextPane, BorderLayout.CENTER);
        noConnectionPanel.add(retryButton, BorderLayout.SOUTH);

        return noConnectionPanel;
    }

    private JPanel buildLoginPanel() {
        final String login = "Veuillez vous identifier.";

        final JTextPane loginTextPane = CenteredTextPaneHandler.create(login);
        loginTextPane.setEditable(false);

        final JButton loginButton = new JButton("Connexion");
        final JButton registerButton = new JButton("Inscription");

        final JTextField username = new JTextField("");
        final JPasswordField password = new JPasswordField("");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                loginButton.setEnabled(false);

                // TODO Don't do this here. Needs to be done by the main thread!
                Window.this.clientController.login(username.getText(), password.getPassword());
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                registerButton.setEnabled(false);

                // TODO Don't do this here. Needs to be done by the main thread!
                Window.this.clientController.register(username.getText(), password.getPassword());
            }
        });

        final JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(loginTextPane, BorderLayout.CENTER);

        final JPanel loginSouthPanel = new JPanel(new BorderLayout());

        final JPanel loginInfo = new JPanel(new GridLayout(2, 2));

        final SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);

        final JTextPane idTextPane = new JTextPane();
        idTextPane.setText("Identifiant :");
        idTextPane.setEditable(false);
        idTextPane.setBackground(Color.LIGHT_GRAY);
        idTextPane.setParagraphAttributes(attributes, true);

        final JTextPane pwdTextPane = new JTextPane();
        pwdTextPane.setText("Mot de passe :");
        pwdTextPane.setEditable(false);
        pwdTextPane.setBackground(Color.LIGHT_GRAY);
        pwdTextPane.setParagraphAttributes(attributes, true);

        loginInfo.add(idTextPane);
        loginInfo.add(username);
        loginInfo.add(pwdTextPane);
        loginInfo.add(password);

        loginSouthPanel.add(loginInfo, BorderLayout.CENTER);

        final JPanel loginButtons = new JPanel(new GridLayout(1, 2));
        loginButtons.add(loginButton);
        loginButtons.add(registerButton);
        loginSouthPanel.add(loginButtons, BorderLayout.SOUTH);

        loginPanel.add(loginSouthPanel, BorderLayout.SOUTH);

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
        this.setPanel(Panel.NO_CONNECTION);
    }

    public void connected() {
        this.setPanel(Panel.LOGIN);
    }
}
