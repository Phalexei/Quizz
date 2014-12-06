package imag.quizz.server.game;

import imag.quizz.common.protocol.Separator;
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
    private long id;

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
    private Map<String, Question[]> questionsA;

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
    private Player playerB;

    /**
     * The themes proposed to the second opponent.
     */
    private String[] themesB;

    /**
     * The questions chosen for the second opponent themes.
     */
    private Map<String, Question[]> questionsB;

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

        this.currentQuestionA = 0;
        this.currentQuestionB = 0;
    }

    private Game() {
        // Empty
    }

    public void playerSelectTheme(final Player player, final int themeIndex) {
        Validate.isTrue(themeIndex < 4, "Invalid themeIndex value (" + themeIndex + ")");
        if (this.playerA == player) {
            this.chosenThemeA = themeIndex;
            this.currentQuestionA = 1;
            this.playerAStatus = PlayerStatus.ANSWER_QUESTION;
            if (this.playerBStatus == PlayerStatus.WAIT) {
                this.playerBStatus = PlayerStatus.ANSWER_QUESTION;
            }
        } else if (this.playerB == player) {
            this.chosenThemeB = themeIndex;
            this.currentQuestionB = 1;
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
            if (this.currentQuestionA < 5) {
                question = this.questionsA.get(this.getThemesA()[this.chosenThemeA])[this.currentQuestionA - 1 - 4];
            } else {
                question = this.questionsB.get(this.getThemesB()[this.chosenThemeB])[this.currentQuestionA - 1 - 4];
            }
            this.currentQuestionA++;
            if (this.currentQuestionA > 8 || this.currentQuestionA > 4 && this.playerBStatus == PlayerStatus.SELECT_THEME) {
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
            if (this.currentQuestionB < 5) {
                question = this.questionsB.get(this.getThemesB()[this.chosenThemeB])[this.currentQuestionB - 1];
            } else {
                question = this.questionsA.get(this.getThemesA()[this.chosenThemeA])[this.currentQuestionB - 1 - 4];
            }
            this.currentQuestionB++;
            if (this.currentQuestionB > 8 || this.currentQuestionB > 4 && this.playerAStatus == PlayerStatus.SELECT_THEME) {
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

    public String toMessageData() {
        return this.toMessageData(1);
    }

    public String toMessageData(final int baseSeparatorLevel) {
        Validate.inclusiveBetween(1, Separator.AMOUNT - 2, baseSeparatorLevel, "Invalid separator level");

        final String[] separators = new String[]{
                Separator.getString(baseSeparatorLevel),
                Separator.getString(baseSeparatorLevel + 1),
                Separator.getString(baseSeparatorLevel + 2),
        };

        final StringBuilder builder = new StringBuilder();
        builder.append(this.id).append(separators[0]);

        builder.append(this.playerA.getLogin()).append(separators[0]);
        builder.append(this.playerB.getLogin()).append(separators[0]);

        builder.append(this.chosenThemeA).append(separators[0]);
        builder.append(this.currentQuestionA).append(separators[0]);
        builder.append(this.playerAScore).append(separators[0]);

        builder.append(this.chosenThemeB).append(separators[0]);
        builder.append(this.currentQuestionB).append(separators[0]);
        builder.append(this.playerBScore).append(separators[0]);

        this.appendThemes(builder, this.themesA, this.questionsA, separators);
        builder.append(separators[0]);

        this.appendThemes(builder, this.themesB, this.questionsB, separators);

        return builder.toString();
    }

    private void appendThemes(
            final StringBuilder builder,
            final String[] themes,
            final Map<String, Question[]> questions,
            final String[] separators
                             ) {
        for (final String theme : themes) {
            builder.append(theme).append(separators[2]);
            final Question[] themeQuestions = questions.get(theme);
            for (final Question question : themeQuestions) {
                builder.append(question.getQuestion()).append(separators[2]);
                for (final String answer : question.getAnswers()) {
                    builder.append(answer).append(separators[2]);
                }
                builder.append(question.getCorrectAnswerIndex());
            }
            builder.append(separators[1]);
        }
    }

    public static Game fromMessageData(final Map<String, Player> players, final String gameData, final int baseSeparatorLevel) {
        Validate.inclusiveBetween(1, Separator.AMOUNT - 2, baseSeparatorLevel, "Invalid separator level");

        final String[] separators = new String[]{
                Separator.getString(baseSeparatorLevel),
                Separator.getString(baseSeparatorLevel + 1),
                Separator.getString(baseSeparatorLevel + 2),
        };

        final String[] firstLevelSplit = gameData.split(separators[0]);
        final long id = Long.parseLong(firstLevelSplit[0]);
        final String loginA = firstLevelSplit[1];
        final String loginB = firstLevelSplit[2];
        final Player playerA = players.get(loginA);
        final Player playerB = players.get(loginB);
        if (playerA == null || playerB == null) {
            throw new IllegalStateException("Unknown player: " + (playerA == null ? loginA : loginB));
        }
        final int chosenThemeA = Integer.parseInt(firstLevelSplit[3]);
        final int currentQuestionA = Integer.parseInt(firstLevelSplit[4]);
        final int playerAScore = Integer.parseInt(firstLevelSplit[5]);
        final int chosenThemeB = Integer.parseInt(firstLevelSplit[6]);
        final int currentQuestionB = Integer.parseInt(firstLevelSplit[7]);
        final int playerBScore = Integer.parseInt(firstLevelSplit[8]);
        final SortedMap<String, Question[]> questionsA = Game.parseThemes(firstLevelSplit[9], separators);
        final SortedMap<String, Question[]> questionsB = Game.parseThemes(firstLevelSplit[10], separators);
        final String[] themesA = new ArrayList<>(questionsA.keySet()).toArray(new String[questionsA.size()]);
        final String[] themesB = new ArrayList<>(questionsB.keySet()).toArray(new String[questionsB.size()]);

        final PlayerStatus playerAStatus;
        if (currentQuestionA == 0) {
            playerAStatus = PlayerStatus.SELECT_THEME;
        } else if (currentQuestionB == 0 && currentQuestionA > 4) {
            playerAStatus = PlayerStatus.WAIT;
        } else {
            playerAStatus = PlayerStatus.ANSWER_QUESTION;
        }
        final PlayerStatus playerBStatus;
        if (currentQuestionB == 0) {
            playerBStatus = PlayerStatus.SELECT_THEME;
        } else if (currentQuestionA == 0 && currentQuestionB > 4) {
            playerBStatus = PlayerStatus.WAIT;
        } else {
            playerBStatus = PlayerStatus.ANSWER_QUESTION;
        }

        final Game game = new Game();
        game.id = id;
        game.playerA = playerA;
        game.themesA = themesA;
        game.questionsA = questionsA;
        game.chosenThemeA = chosenThemeA;
        game.playerAStatus = playerAStatus;
        game.playerAScore = playerAScore;
        game.currentQuestionA = currentQuestionA;
        game.playerB = playerB;
        game.themesB = themesB;
        game.questionsB = questionsB;
        game.chosenThemeB = chosenThemeB;
        game.playerBStatus = playerBStatus;
        game.playerBScore = playerBScore;
        game.currentQuestionB = currentQuestionB;

        return game;
    }

    private static SortedMap<String, Question[]> parseThemes(final String themes, final String[] separators) {
        final SortedMap<String, Question[]> result = new TreeMap<>();
        final String[] themesSplit = themes.split(separators[1]);
        for (final String themeData : themesSplit) {
            final String[] themeSplit = themeData.split(separators[2]);
            final String theme = themeSplit[0];
            final int questionAmount = (themeSplit.length - 1) / 6;
            final Question[] questions = new Question[questionAmount];
            for (int i = 0; i < questionAmount; i++) {
                final int j = 6 * i + 1;
                final String questionString = themeSplit[j];
                final String[] answers = new String[]{
                        themeSplit[j + 1],
                        themeSplit[j + 2],
                        themeSplit[j + 3],
                        themeSplit[j + 4]
                };
                final int correctAnswerIndex = Integer.parseInt(themeSplit[j + 5]);
                final Question question = new Question(questionString, answers, correctAnswerIndex);
                questions[i] = question;
            }
            result.put(theme, questions);
        }
        return result;
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
