package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class WaitMessage extends Message {

    public WaitMessage(final long senderId) {
        super(Command.WAIT, senderId);
    }

    /* package */ WaitMessage(String[] messageSplit) {
        super(Command.WAIT, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
