package imag.quizz.server.game;

import imag.quizz.server.game.QuestionBase.Question;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a Game.
 */
public final class Game {

    private static final Random RANDOM = new Random();

    private static long randomId() {
        return Game.RANDOM.nextLong();
    }

    /**
     * A unique Game identifier.
     */
    private final long id;

    /**
     * The first of the 2 opponents.
     */
    private final Player playerA;

    /**
     * The themes proposed to the first opponent.
     */
    private final String[] themesA;

    /**
     * The questions chosen for the first opponent themes.
     */
    private final Map<String, List<Question>> questionsA;

    /**
     * The theme chosen by the first opponent.
     */
    private int chosenThemeA;

    /**
     * The next question the first opponent should answer.
     */
    private int currentQuestionA;

    /**
     * The second of the 2 opponents.
     */
    private final Player playerB;

    /**
     * The themes proposed to the second opponent.
     */
    private final String[] themesB;

    /**
     * The questions chosen for the second opponent themes.
     */
    private final Map<String, List<Question>> questionsB;

    /**
     * The theme chosen by the second opponent.
     */
    private int chosenThemeB;

    /**
     * The next question the second opponent should answer.
     */
    private int currentQuestionB;

    public Game(final Player playerA, final Player playerB) {
        this.id = Game.randomId();

        this.playerA = playerA;
        this.playerB = playerB;

        this.themesA = new String[4];
        this.themesB = new String[4];

        this.questionsA = new LinkedHashMap<>();
        this.questionsB = new LinkedHashMap<>();

        this.chosenThemeA = -1;
        this.chosenThemeB = -1;

        this.currentQuestionA = -1;
        this.currentQuestionB = -1;
    }

    public Player getOpponent(final Player player) {
        if (this.playerA == player) {
            return this.playerB;
        } else if (this.playerB == player) {
            return this.playerA;
        } else {
            throw new IllegalArgumentException("Player " + player.getLogin() + " isn't part of this Game!");
        }
    }

    public Player getPlayerA() {
        return this.playerA;
    }

    public String[] getThemesA() {
        return this.themesA;
    }

    public Map<String, List<Question>> getQuestionsA() {
        return this.questionsA;
    }

    public int getChosenThemeA() {
        return this.chosenThemeA;
    }

    public int getCurrentQuestionA() {
        return this.currentQuestionA;
    }

    public Player getPlayerB() {
        return this.playerB;
    }

    public String[] getThemesB() {
        return this.themesB;
    }

    public Map<String, List<Question>> getQuestionsB() {
        return this.questionsB;
    }

    public int getChosenThemeB() {
        return this.chosenThemeB;
    }

    public int getCurrentQuestionB() {
        return this.currentQuestionB;
    }

    public void setChosenThemeA(final int chosenThemeA) {
        this.chosenThemeA = chosenThemeA;
    }

    public void setCurrentQuestionA(final int currentQuestionA) {
        this.currentQuestionA = currentQuestionA;
    }

    public void setChosenThemeB(final int chosenThemeB) {
        this.chosenThemeB = chosenThemeB;
    }

    public void setCurrentQuestionB(final int currentQuestionB) {
        this.currentQuestionB = currentQuestionB;
    }
}
