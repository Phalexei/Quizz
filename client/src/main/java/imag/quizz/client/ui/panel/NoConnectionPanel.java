package imag.quizz.client.ui.panel;

import imag.quizz.client.game.ClientController;
import imag.quizz.client.ui.CenteredTextPaneHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoConnectionPanel extends Panel {
    public NoConnectionPanel(final ClientController clientController) {
        final String noConnection = "Aucun serveur disponible.";

        final JTextPane noConnectionTextPane = CenteredTextPaneHandler.create(noConnection);
        noConnectionTextPane.setEditable(false);

        final JButton retryButton = new JButton("Réessayer");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                retryButton.setEnabled(false);

                // TODO Don't do this here. Needs to be done by the main thread!
                clientController.connect();
            }
        });

        final JPanel noConnectionPanel = new JPanel();
        final BorderLayout layout = new BorderLayout();
        noConnectionPanel.setLayout(layout);
        noConnectionPanel.add(noConnectionTextPane, BorderLayout.CENTER);
        noConnectionPanel.add(retryButton, BorderLayout.SOUTH);
    }

    @Override
    public void showError(String error) {
    }
}
