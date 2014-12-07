package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class OkMessage extends OkNokMessage {

    public OkMessage(final long senderId, final Message source) {
        this(senderId, null, source);
    }

    public OkMessage(final long senderId, final String text, final Message source) {
        super(Command.OK, senderId, text, source);
    }

    /* package */ OkMessage(final String[] messageSplit) {
        super(Command.OK, messageSplit);
    }
}
