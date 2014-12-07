package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class OkMessage extends OkNokMessage {

    public OkMessage(final long sourceId, final Message source) {
        this(sourceId, null, source);
    }

    public OkMessage(final long sourceId, final String text, final Message source) {
        super(Command.OK, sourceId, text, source);
    }

    /* package */ OkMessage(final String[] messageSplit) {
        super(Command.OK, messageSplit);
    }
}
