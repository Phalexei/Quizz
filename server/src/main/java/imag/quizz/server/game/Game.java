package imag.quizz.server.game;

/**
 * Represents a Game.
 */
public final class Game {

    /**
     * The first of the 2 opponents.
     */
    private Player playerA;

    /**
     * The themes proposed to the first opponent.
     */
    private String[] themesA;

    /**
     * The questions chosen for the first opponent themes.
     */
    private String[][] questionsA;

    /**
     * The theme chosen by the first opponent.
     */
    private byte chosenThemeA;

    /**
     * The next question the first opponent should answer.
     */
    private byte currentQuestionA;

    /**
     * The second of the 2 opponents.
     */
    private Player playerB;

    /**
     * The themes proposed to the second opponent.
     */
    private String[] themesB;

    /**
     * The questions chosen for the second opponent themes.
     */
    private String[][] questionsB;

    /**
     * The theme chosen by the second opponent.
     */
    private byte chosenThemeB;

    /**
     * The next question the second opponent should answer.
     */
    private byte currentQuestionB;

    public Game(Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;

        //TODO: generate questions
    }
}
