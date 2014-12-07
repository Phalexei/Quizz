package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NoAnswerMessage extends Message {

    public NoAnswerMessage(final long senderId) {
        super(Command.NOANSWER, senderId);
    }

    /* package */ NoAnswerMessage(final String[] messageSplit) {
        super(Command.NOANSWER, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
