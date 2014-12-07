package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class DropMessage extends Message {

    public DropMessage(final long senderId) {
        super(Command.DROP, senderId);
    }

    /* package */ DropMessage(final String[] messageSplit) {
        super(Command.DROP, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
