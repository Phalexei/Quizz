package imag.quizz.client.ui.panel;

import imag.quizz.client.ClientController;
import imag.quizz.client.ui.CenteredTextPaneHandler;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGamePanel extends Panel {

    private final JTextPane newGameTextPane;

    public NewGamePanel(final ClientController clientController) {
        super(new BorderLayout());
        final String newGame = "Entrez le pseudo d'un adversaire. Laissez blanc pour un adversaire aléatoire";
        this.newGameTextPane = CenteredTextPaneHandler.create(newGame);
        this.newGameTextPane.setEditable(false);

        this.add(this.newGameTextPane, BorderLayout.CENTER);

        final JPanel newGameSouthPanel = new JPanel(new BorderLayout());

        final JPanel newGameInfo = new JPanel(new GridLayout(1, 2));

        final SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);

        final JTextPane idTextPane = new JTextPane();
        idTextPane.setText("Identifiant :");
        idTextPane.setEditable(false);
        idTextPane.setBackground(Color.LIGHT_GRAY);
        idTextPane.setParagraphAttributes(attributes, true);

        final JTextField username = new JTextField("");

        newGameInfo.add(idTextPane);
        newGameInfo.add(username);

        newGameSouthPanel.add(newGameInfo, BorderLayout.CENTER);

        final JPanel buttons = new JPanel(new GridLayout(1, 2));
        final JButton newButton = new JButton("Nouvelle partie");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.newGame(username.getText());
            }
        });
        buttons.add(newButton);

        final JButton gamesButton = new JButton("Parties en cours");
        gamesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.askGames();
            }
        });
        buttons.add(gamesButton);

        newGameSouthPanel.add(buttons, BorderLayout.SOUTH);

        this.add(newGameSouthPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showError(String error) {
        this.newGameTextPane.setText(error);
    }
}
