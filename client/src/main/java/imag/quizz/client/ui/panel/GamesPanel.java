package imag.quizz.client.ui.panel;

import imag.quizz.client.game.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class GamesPanel extends Panel {

    private class Game {
        private final long    id;
        private final String  opponent;
        private final boolean wait;
        private final int     myScore;
        private final int     myCurrentQuestion;
        private final int     oppScore;
        private final int     oppCurrentQuestion;

        private Game(long id, boolean wait, int myScore, int myCurrentQuestion, String opponent, int oppScore, int oppCurrentQuestion) {
            this.id = id;
            this.opponent = opponent;
            this.wait = wait;
            this.myScore = myScore;
            this.myCurrentQuestion = myCurrentQuestion;
            this.oppScore = oppScore;
            this.oppCurrentQuestion = oppCurrentQuestion;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();

            s.append("Partie contre : ").append(this.opponent).append(".\n");

            if (this.myCurrentQuestion == 9) { // we're done with questions
                if (this.oppCurrentQuestion == 9) { // opponent is done too, Game is over
                    s.append("Terminée.\n");
                } else {
                    s.append("En attente du thème de l'adversaire.\n");
                }
            } else { // we still may have questions to answer
                if (!this.wait) {
                    s.append("Nouvelles questions !\n");
                } else {
                    s.append("En attente de l'adversaire.\n");
                }
            }

            s.append("Score\n").append("Vous ").append(this.myScore).append(" : ").append(this.oppScore).append(" ").append(this.opponent);

            return s.toString();
        }

        public long getId() {
            return this.id;
        }
    }

    private class GameCellRenderer extends JLabel implements ListCellRenderer<Object> {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String s = value.toString();
            setText(s);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
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
            public void actionPerformed(ActionEvent e) {
                clientController.newGame();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int index = GamesPanel.this.gamesList.getSelectedIndex();
                if (index != -1) { // something is selected
                    clientController.play(GamesPanel.this.listModel.get(index).getId());
                }
            }
        });

        this.gamesList.setCellRenderer(new GameCellRenderer());

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(this.gamesList);

        this.add(newGameButton, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(playButton, BorderLayout.SOUTH);

        this.gameIDs = new HashMap<>();
    }

    @Override
    public void showError(String error) {
        //nothing
    }

    public void addGame(long gameId, boolean wait, int myScore, int myCurrentQuestion, String opponent, int oppScore, int oppCurrentQuestion) {
        this.gameIDs.put(gameId, this.listCounter);
        this.listModel.add(this.listCounter++, new Game(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion));
    }

    public void clearGames() {
        this.listModel.clear();
        this.listCounter = 0;
        this.gameIDs.clear();
    }

    public void updateGame(long gameId, boolean wait, int myScore, int myCurrentQuestion, String opponent, int oppScore, int oppCurrentQuestion) {
        Integer index = this.gameIDs.get(gameId);
        if (index != null) {
            this.listModel.remove(index);
            this.listModel.add(index, new Game(gameId, wait, myScore, myCurrentQuestion, opponent, oppScore, oppCurrentQuestion));
        }
    }
}
