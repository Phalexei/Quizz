package imag.quizz.client.ui.panel;

import imag.quizz.client.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GamesPanel extends Panel {

    private final class Game {
        private final long    id;
        private final String  opponent;
        private final boolean wait;
        private final int     myScore;
        private final int     myCurrentQuestion;
        private final int     oppScore;
        private final int     oppCurrentQuestion;

        private Game(final long id, final boolean wait, final int myScore, final int myCurrentQuestion, final String opponent, final int oppScore, final int oppCurrentQuestion) {
            this.id = id;
            this.opponent = opponent;
            this.wait = wait;
            this.myScore = myScore;
            this.myCurrentQuestion = myCurrentQuestion;
            this.oppScore = oppScore;
            this.oppCurrentQuestion = oppCurrentQuestion;
        }

        public boolean readyToPlay() {
            return !this.wait && this.myCurrentQuestion < 9;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder();

            s.append("Partie contre : ").append(this.opponent).append(".\t");

            if (this.myCurrentQuestion == 9) { // we're done with questions
                if (this.oppCurrentQuestion == 9) { // opponent is done too, Game is over
                    s.append("Terminée.\t");
                } else {
                    s.append("En attente du thème de l'adversaire.\t");
                }
            } else { // we still may have questions to answer
                if (!this.wait) {
                    s.append("Nouvelles questions !\t");
                } else {
                    s.append("En attente de l'adversaire.\t");
                }
            }

            s.append("Score\t").append("Vous ").append(this.myScore).append(" : ").append(this.oppScore).append(" ").append(this.opponent);

            return s.toString();
        }

        public long getId() {
            return this.id;
        }
    }

    private class GameCellRenderer extends JLabel implements ListCellRenderer<Object> {
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final String s = value.toString();
            this.setText(s);
            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            } else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }
            this.setEnabled(list.isEnabled());
            this.setFont(list.getFont());
            this.setOpaque(true);
            return this;
        }
    }

    private final JList<Game>            gamesList;
    private final DefaultListModel<Game> listModel;
    private       int                    listCounter;
    // game ID; index in list;
    private final Map<Long, Integer>     gameIDs;

    public GamesPanel(final ClientController clientController) {
        super(new BorderLayout());

        final JButton playButton = new JButton("JOUER !");
        final JButton newGameButton = new JButton("Nouvelle Partie");

        this.listModel = new DefaultListModel<>();
        this.gamesList = new JList<>(this.listModel);
        this.listCounter = 0;

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                clientController.newGame();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int index = GamesPanel.this.gamesList.getSelectedIndex();
                if (index != -1) { // something is selected
                    final Game game = GamesPanel.this.listModel.get(index);
                    if (game.readyToPlay()) {
                        clientController.play(game.getId());
                    }
                }
            }
        });

        final JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.add(playButton);
        buttons.add(newGameButton);

        this.gamesList.setCellRenderer(new GameCellRenderer());

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(this.gamesList);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.SOUTH);

        this.gameIDs = new HashMap<>();
    }

    @Override
    public void showError(final String error) {
        //nothing
    }

    public void addGame(final long gameId, final boolean wait, final int myScore, final int myCurrentQuestion, final String opponent, final int oppScore, final int oppCurrentQuestion) {
        this.gameIDs.put(gameId, this.listCounter);
        this.listModel.add(this.listCounter++, new Game(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion));
    }

    public void clearGames() {
        this.listModel.clear();
        this.listCounter = 0;
        this.gameIDs.clear();
    }

    public void updateGame(final long gameId, final boolean wait, final int myScore, final int myCurrentQuestion, final String opponent, final int oppScore, final int oppCurrentQuestion) {
        final Integer index = this.gameIDs.get(gameId);
        if (index != null) {
            this.listModel.remove(index);
            this.listModel.add(index, new Game(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion));
        }
    }
}
