package imag.quizz.client.ui;

import imag.quizz.client.game.Manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Map button clicks to the game's {@link imag.quizz.client.game.Manager}.
 */
public class InputHandler implements ActionListener {

    private final Manager manager;

    public InputHandler(final Manager manager) {
        this.manager = manager;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        this.manager.onButtonClick(event.getActionCommand());
    }
}
