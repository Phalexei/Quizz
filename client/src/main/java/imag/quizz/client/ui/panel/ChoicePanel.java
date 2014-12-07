package imag.quizz.client.ui.panel;

import imag.quizz.client.game.ClientController;
import imag.quizz.client.ui.CenteredTextPaneHandler;
import imag.quizz.client.ui.InputHandler;

import javax.swing.*;
import java.awt.*;

public class ChoicePanel extends Panel {

    private final JButton topLeftButton;
    private final JButton   topRightButton;
    private final JButton   bottomLeftButton;
    private final JButton   bottomRightButton;

    private final JTextPane questionTextPane;

    public ChoicePanel(final ClientController clientController) {
        this.topLeftButton = new JButton();
        this.topRightButton = new JButton();
        this.bottomLeftButton = new JButton();
        this.bottomRightButton = new JButton();

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

        this.questionTextPane = CenteredTextPaneHandler.create("");
        this.questionTextPane.setEditable(false);

        this.setLayout(new GridLayout(2, 1));
        this.add(this.questionTextPane);
        this.add(buttonsPanel);
    }
    @Override
    public void showError(final String error) {
        this.toggleButtons(false);
        this.setQuestion(error);
    }

    public void toggleButtons(final boolean enabled) {
        this.topLeftButton.setEnabled(enabled);
        this.topRightButton.setEnabled(enabled);
        this.bottomLeftButton.setEnabled(enabled);
        this.bottomRightButton.setEnabled(enabled);
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
}
