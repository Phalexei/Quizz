package imag.quizz.client.ui.panel;

import imag.quizz.client.ui.CenteredTextPaneHandler;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends Panel {

    public WelcomePanel() {
        final String welcome = "Bienvenue sur Quizz Duel\n\nConnexion en cours...";
        final JTextPane welcomeTextPane = CenteredTextPaneHandler.create(welcome);
        welcomeTextPane.setEditable(false);

        this.setLayout(new GridLayout(1, 1));
        this.add(welcomeTextPane);
    }

    @Override
    public void showError(String error) {
        // do nothing
    }
}
