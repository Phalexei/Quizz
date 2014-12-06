package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class EndMessage extends Message {
    private final int userScore;
    private final int opponentScore;

    public EndMessage(final int senderId, final int userScore, final int opponentScore) {
        super(Command.END, senderId);
        this.userScore = userScore;
        this.opponentScore = opponentScore;
    }

    public EndMessage(String[] messageSplit) {
        super(Command.END, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.userScore = Integer.parseInt(messageSplit[2]);
        this.opponentScore = Integer.parseInt(messageSplit[3]);
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public int getUserScore() {
        return userScore;
    }

    @Override
    protected String getParametersString() {
        return this.userScore + Separator.LEVEL_1S + this.opponentScore;
    }
}
