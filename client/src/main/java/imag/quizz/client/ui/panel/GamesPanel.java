package imag.quizz.client.ui.panel;

import imag.quizz.client.game.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GamesPanel extends Panel {

    private class Game {
        private final long id;
        private final String opponent;
        private final boolean wait;
        private final int myScore;
        private final int myCurrentQuestion;
        private final int oppScore;
        private final int oppCurrentQuestion;

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

    private final JScrollPane            scrollPane;
    private final JList<Game>            gamesList;
    private final JButton                playButton;
    private final DefaultListModel<Game> listModel;
    private       int                    listCounter;
    // game ID; index in list;
    private final Map<Long, Integer>     gameIDs;

    public GamesPanel(final ClientController clientController) {
        super(new BorderLayout());

        this.playButton = new JButton("JOUER");
        this.listModel = new DefaultListModel<>();
        this.gamesList = new JList<>(this.listModel);
        this.listCounter = 0;

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = GamesPanel.this.gamesList.locationToIndex(e.getPoint());
                    System.out.println("Double clicked on Item " + index);
                    //TODO: find ID of game based on click
                    //clientController.play();
                }
            }
        };
        this.gamesList.addMouseListener(mouseListener);

        this.gamesList.setCellRenderer(new GameCellRenderer());

        this.scrollPane = new JScrollPane();
        this.scrollPane.getViewport().setView(this.gamesList);

        this.add(this.scrollPane);
        this.add(this.playButton);

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
