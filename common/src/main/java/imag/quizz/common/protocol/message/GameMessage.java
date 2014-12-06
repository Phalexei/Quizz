package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class GameMessage extends Message {

   /* A game is composed of the 2 user names and the 2 sets of themes IDs with their associated
    sets of questions IDs, separated by Separator.LEVEL_2s. Each set of question IDs is separated
    by Separator.LEVEL_1s.*/

    private final long gameId;
    private final String playerA, playerB;
    private final int themeA, themeB;
    private final int[] questionsA, questionsB;


    public GameMessage(final int senderId, final int gameId, final String playerA, final String playerB,
                       final int themeA, final int themeB, final int[] questionsA, final int[] questionsB) {
        super(Command.GAME, senderId);
        this.gameId = gameId;
        this.playerA = playerA;
        this.playerB = playerB;
        this.themeA = themeA;
        this.themeB = themeB;
        this.questionsA = questionsA;
        this.questionsB = questionsB;
    }

    public GameMessage(String[] messageSplit) {
        super(Command.GAME, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.gameId = Long.parseLong(messageSplit[2]);
        this.playerA = messageSplit[3];
        this.playerB = messageSplit[4];
        this.themeA = Integer.parseInt(messageSplit[5]);
        this.themeB = Integer.parseInt(messageSplit[6]);
        this.questionsA = getQuestionsId(messageSplit[7]);
        this.questionsB = getQuestionsId(messageSplit[8]);
    }

    @Override
    protected String getParametersString() {
        return this.gameId + Separator.LEVEL_1S + this.playerA + Separator.LEVEL_1S +
                this.playerB + Separator.LEVEL_1S + this.themeA + Separator.LEVEL_1S +
                this.themeB + Separator.LEVEL_1S + this.getQuestionsString(questionsA)
                + Separator.LEVEL_1S + this.getQuestionsString(questionsB);
    }

    /**
     * Creates a list of question ID separated by {@link Separator#LEVEL_2}
     * @param questions an array of question IDs
     * @return a correctly formated String of IDs
     */
    private String getQuestionsString(int[] questions) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < questions.length - 1; i++) {
            s.append(questions[i]);
            s.append(Separator.LEVEL_2S);
        }
        s.append(questions[questions.length - 1]);

        return s.toString();
    }

    /**
     * Creates an array of question IDs from a String
     * @param questions a string containing question IDs separated by {@link Separator#LEVEL_2}
     * @return an array of question IDs
     */
    private int[] getQuestionsId(String questions) {
        final String[] split = questions.split(Separator.LEVEL_2S);
        final int[] result = new int[split.length];

        int i = 0;
        for (String question : split) {
            result[i++] = Integer.parseInt(question);
        }

        return result;
    }


    public long getGameId() {
        return gameId;
    }

    public String getPlayerA() {
        return playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public int getThemeA() {
        return themeA;
    }

    public int getThemeB() {
        return themeB;
    }

    public int[] getQuestionsA() {
        return questionsA;
    }

    public int[] getQuestionsB() {
        return questionsB;
    }
}
