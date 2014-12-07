package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NokMessage extends Message {

    private final String errorMessage;

    public NokMessage(final long senderId) {
        super(Command.NOK, senderId);
        this.errorMessage = Long.toString(System.nanoTime());
    }

    /* package */ NokMessage(final String[] messageSplit) {
        super(Command.NOK, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.errorMessage = messageSplit[2];
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    protected String getParametersString() {
        return this.errorMessage;
    }
}
