package imag.quizz.client.ui.panel;

import imag.quizz.client.game.ClientController;
import imag.quizz.client.ui.CenteredTextPaneHandler;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends Panel {

    private final JTextPane loginTextPane;

    public LoginPanel(final ClientController clientController) {
        super(new BorderLayout());
        final String login = "Veuillez vous identifier.";

        this.loginTextPane = CenteredTextPaneHandler.create(login);
        this.loginTextPane.setEditable(false);

        final JButton loginButton = new JButton("Connexion");
        final JButton registerButton = new JButton("Inscription");

        final JTextField username = new JTextField("");
        final JPasswordField password = new JPasswordField("");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                clientController.login(username.getText(), password.getPassword());
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                clientController.register(username.getText(), password.getPassword());
            }
        });

        this.add(this.loginTextPane, BorderLayout.CENTER);

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

        this.add(loginSouthPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showError(String error) {
        this.loginTextPane.setText(error);
    }
}
