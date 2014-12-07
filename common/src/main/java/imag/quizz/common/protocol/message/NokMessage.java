package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NokMessage extends OkNokMessage {

    public NokMessage(final long senderId, final Message source) {
        this(senderId, null, source);
    }

    public NokMessage(final long senderId, final String text, final Message source) {
        super(Command.NOK, senderId, text, source);
    }

    /* package */ NokMessage(final String[] messageSplit) {
        super(Command.NOK, messageSplit);
    }
}
