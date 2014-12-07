package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NoAnswerMessage extends Message {

    private final long playerId;

    public NoAnswerMessage(final long senderId, final long playerId) {
        super(Command.NOANSWER, senderId);
        this.playerId = playerId;
    }
    /* package */ NoAnswerMessage(String[] messageSplit) {
        super(Command.NOANSWER, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.playerId = Long.parseLong(messageSplit[2]);
    }

    @Override
    protected String getParametersString() {
        return Long.toString(this.playerId);
    }
}
