package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class OkMessage extends Message {

    public OkMessage() {
        super(Command.OK);
    }

    @Override
    protected String getParametersString() {
        return null;
    }
}
