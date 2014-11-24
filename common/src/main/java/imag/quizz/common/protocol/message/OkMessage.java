package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class OkMessage extends Message {

    public OkMessage() {
        super(Command.OK);
    }

    /* package*/ OkMessage(final String[] messageSplit) {
        super(Command.NOK);
        this.checkCommandName(messageSplit[0]);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
