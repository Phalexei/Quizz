package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class WaitMessage extends Message {

    private final String text;

    public WaitMessage(final long senderId, final String text) {
        super(Command.WAIT, senderId);
        this.text = text;
    }

    /* package */ WaitMessage(final String[] messageSplit) {
        super(Command.WAIT, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.text = messageSplit[2];
    }

    public String getText() {
        return this.text;
    }

    @Override
    protected String getParametersString() {
        return this.text;
    }
}
