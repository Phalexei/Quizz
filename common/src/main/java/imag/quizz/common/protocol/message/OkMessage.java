package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class OkMessage extends Message {

    public OkMessage(final long senderId) {
        super(Command.OK, senderId);
    }

    /* package */ OkMessage(final String[] messageSplit) {
        super(Command.OK, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
