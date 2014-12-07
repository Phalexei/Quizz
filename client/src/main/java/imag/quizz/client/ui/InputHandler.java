package imag.quizz.client.ui;

import imag.quizz.client.game.ClientController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Map button clicks to the game's {@link imag.quizz.client.game.ClientController}.
 */
public class InputHandler implements ActionListener {

    private final ClientController clientController;

    public InputHandler(final ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        this.clientController.answerSelected(event.getActionCommand());
    }
}
