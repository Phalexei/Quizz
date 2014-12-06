package imag.quizz.server.game;

import imag.quizz.server.game.QuestionBase.Question;
import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * Represents a Game.
 */
public final class Game {

    public enum PlayerStatus {
        /**
         * If the player has to select a theme
         */
        SELECT_THEME,

        /**
         * If the player has to answer a question
         */
        ANSWER_QUESTION,

        /**
         * If the player has to wait for the opponent to do something
         */
        WAIT
    }

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
    private final Map<String, Question[]> questionsA;

    /**
     * The theme chosen by the first opponent.
     */
    private int chosenThemeA;

    /**
     * The status of the first opponent.
     */
    private PlayerStatus playerAStatus;

    /**
     * The score of the first opponent.
     */
    private int playerAScore;

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
    private final Map<String, Question[]> questionsB;

    /**
     * The status of the second opponent.
     */
    private PlayerStatus playerBStatus;

    /**
     * The score of the second opponent.
     */
    private int playerBScore;

    /**
     * The theme chosen by the second opponent.
     */
    private int chosenThemeB;

    /**
     * The next question the second opponent should answer.
     */
    private int currentQuestionB;

    public Game(final QuestionBase base, final Player playerA, final Player playerB) {
        this.id = Game.randomId();

        this.playerA = playerA;
        this.playerB = playerB;

        final LinkedList<String> themes = new LinkedList<>(base.getThemes().keySet());
        Collections.shuffle(themes);

        this.themesA = new String[]{
                themes.poll(),
                themes.poll(),
                themes.poll(),
                themes.poll()
        };
        this.themesB = new String[]{
                themes.poll(),
                themes.poll(),
                themes.poll(),
                themes.poll()
        };

        this.questionsA = Collections.unmodifiableMap(new HashMap<String, Question[]>() {{
            for (final String theme : Game.this.themesA) {
                final LinkedList<Question> availableQuestions = new LinkedList<>(base.getThemes().get(theme));
                Collections.shuffle(availableQuestions);
                Game.this.questionsA.put(theme, new Question[]{
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll()
                });
            }
        }});
        this.questionsB = Collections.unmodifiableMap(new HashMap<String, Question[]>() {{
            for (final String theme : Game.this.themesB) {
                final LinkedList<Question> availableQuestions = new LinkedList<>(base.getThemes().get(theme));
                Collections.shuffle(availableQuestions);
                Game.this.questionsB.put(theme, new Question[]{
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll(),
                        availableQuestions.poll()
                });
            }
        }});

        this.chosenThemeA = -1;
        this.chosenThemeB = -1;

        this.playerAStatus = PlayerStatus.SELECT_THEME;
        this.playerBStatus = PlayerStatus.SELECT_THEME;

        this.playerAScore = 0;
        this.playerBScore = 0;

        this.currentQuestionA = -1;
        this.currentQuestionB = -1;
    }

    public void playerSelectTheme(final Player player, final int themeIndex) {
        Validate.isTrue(themeIndex < 4, "Invalid themeIndex value (" + themeIndex + ")");
        if (this.playerA == player) {
            this.chosenThemeA = themeIndex;
            this.currentQuestionA = 0;
            this.playerAStatus = PlayerStatus.ANSWER_QUESTION;
            if (this.playerBStatus == PlayerStatus.WAIT) {
                this.playerBStatus = PlayerStatus.ANSWER_QUESTION;
            }
        } else if (this.playerB == player) {
            this.chosenThemeB = themeIndex;
            this.currentQuestionB = 0;
            this.playerBStatus = PlayerStatus.ANSWER_QUESTION;
            if (this.playerAStatus == PlayerStatus.WAIT) {
                this.playerAStatus = PlayerStatus.ANSWER_QUESTION;
            }
        } else {
            throw new IllegalArgumentException("Player " + player.getLogin() + " isn't part of this Game!");
        }
    }

    public boolean playerSelectAnswer(final Player player, final int answerIndex) {
        Validate.isTrue(answerIndex < 4, "Invalid answerIndex value (" + answerIndex + ")");
        if (this.playerA == player) {
            final Question question;
            if (this.currentQuestionA < 4) {
                question = this.questionsA.get(this.getThemesA()[this.chosenThemeA])[this.currentQuestionA];
            } else {
                question = this.questionsB.get(this.getThemesB()[this.chosenThemeB])[this.currentQuestionA - 4];
            }
            this.currentQuestionA++;
            if (this.currentQuestionA > 7 || this.currentQuestionA > 3 && this.playerBStatus == PlayerStatus.SELECT_THEME) {
                this.playerAStatus = PlayerStatus.WAIT;
            }
            if (question.getCorrectAnswerIndex() == answerIndex) {
                this.playerAScore++;
                return true;
            } else {
                return false;
            }
        } else if (this.playerB == player) {
            final Question question;
            if (this.currentQuestionB < 4) {
                question = this.questionsB.get(this.getThemesB()[this.chosenThemeB])[this.currentQuestionB];
            } else {
                question = this.questionsA.get(this.getThemesA()[this.chosenThemeA])[this.currentQuestionB - 4];
            }
            this.currentQuestionB++;
            if (this.currentQuestionB > 7 || this.currentQuestionB > 3 && this.playerAStatus == PlayerStatus.SELECT_THEME) {
                this.playerAStatus = PlayerStatus.WAIT;
            }
            if (question.getCorrectAnswerIndex() == answerIndex) {
                this.playerBScore++;
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Player " + player.getLogin() + " isn't part of this Game!");
        }
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

    public long getId() {
        return this.id;
    }

    public Player getPlayerA() {
        return this.playerA;
    }

    public String[] getThemesA() {
        return this.themesA;
    }

    public Map<String, Question[]> getQuestionsA() {
        return this.questionsA;
    }

    public int getChosenThemeA() {
        return this.chosenThemeA;
    }

    public PlayerStatus getPlayerAStatus() {
        return this.playerAStatus;
    }

    public int getPlayerAScore() {
        return this.playerAScore;
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

    public Map<String, Question[]> getQuestionsB() {
        return this.questionsB;
    }

    public int getChosenThemeB() {
        return this.chosenThemeB;
    }

    public PlayerStatus getPlayerBStatus() {
        return this.playerBStatus;
    }

    public int getPlayerBScore() {
        return this.playerBScore;
    }

    public int getCurrentQuestionB() {
        return this.currentQuestionB;
    }
}
