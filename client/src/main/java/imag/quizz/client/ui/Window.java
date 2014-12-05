package imag.quizz.client.ui;

import imag.quizz.client.game.ClientController;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;

public class Window {

    private static final int WINDOW_WIDTH   = 1440;
    private static final int WINDOW_HEIGHT  = 900;
    private static final int LOG_KEEP_LINES = 1000;

    private final JButton   topLeftButton;
    private final JButton   topRightButton;
    private final JButton   bottomLeftButton;
    private final JButton   bottomRightButton;
    private final JTextPane questionTextPane;
    private final JTextArea logsTextArea;

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

        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(2, 1));
        leftPanel.add(this.questionTextPane);
        leftPanel.add(buttonsPanel);

        this.logsTextArea = new JTextArea();
        this.logsTextArea.setEditable(false);
        final JScrollPane logsScrollPane = new JScrollPane(this.logsTextArea);
        logsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 1));
        rightPanel.add(logsScrollPane);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        final JFrame frame = new JFrame("Quizz");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        new Log4JAppender(this);

        clientController.setWindow(this);
        clientController.start();
        clientController.connect();
    }

    public void setQuestion(final String question) {
        CenteredTextPaneHandler.setText(this.questionTextPane, question);
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
        this.lockButtons();
        // TODO: show popup informing that no connection is available. Add a "Close" and "Retry" button ?
    }
}
