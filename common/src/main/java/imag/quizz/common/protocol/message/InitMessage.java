package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class InitMessage extends Message {

    // TODO Add Data

    public InitMessage(final int senderId /* TODO Data arguments */) {
        super(Command.INIT, senderId);
        // TODO Add Data
    }

    /* package */ InitMessage(final String[] messageSplit) {
        super(Command.INIT, Integer.parseInt(messageSplit[1]));
        // TODO Parse Data
    }

    // TODO Add Data Getters

    @Override
    protected String getParametersString() {
        return null; // TODO Add Data
    }
}
