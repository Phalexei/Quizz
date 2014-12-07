package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NokMessage extends OkNokMessage {

    public NokMessage(final long sourceId, final Message source) {
        this(sourceId, null, source);
    }

    public NokMessage(final long sourceId, final String text, final Message source) {
        super(Command.NOK, sourceId, text, source);
    }

    /* package */ NokMessage(final String[] messageSplit) {
        super(Command.NOK, messageSplit);
    }
}
