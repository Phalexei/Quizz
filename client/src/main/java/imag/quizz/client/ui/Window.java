package imag.quizz.client.ui;

import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.message.PingMessage;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Window {

    private static final int WINDOW_WIDTH = 1440;
    private static final int WINDOW_HEIGHT = 900;
    private static final int LOG_KEEP_LINES = 1000;

    private final JButton topLeftButton;
    private final JButton topRightButton;
    private final JButton bottomLeftButton;
    private final JButton bottomRightButton;
    private final JTextPane questionTextPane;
    private final JTextArea logsTextArea;

    public Window(final SocketHandler handler) {
        this.topLeftButton = new JButton("Oui");
        this.topRightButton = new JButton("Non");
        this.bottomLeftButton = new JButton("Peut-être");
        this.bottomRightButton = new JButton("42");

        // TODO
        final ActionListener a = new ActionListener() {

            private final Random random = new Random();

            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("PING");
                handler.write(new PingMessage().toString() + "\n");
            }
        };
        this.topLeftButton.addActionListener(a);
        this.topRightButton.addActionListener(a);
        this.bottomLeftButton.addActionListener(a);
        this.bottomRightButton.addActionListener(a);

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
}
