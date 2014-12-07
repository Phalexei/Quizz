package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NewMessage extends Message {

    private final String opponent;

    public NewMessage(final long sourceId) {
        this(sourceId, null);
    }

    public NewMessage(final long sourceId, final String opponent) {
        super(Command.NEW, sourceId);
        this.opponent = opponent;
    }

    /* package */ NewMessage(final String[] messageSplit) {
        super(Command.NEW, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        if (messageSplit.length > 2) {
            this.opponent = messageSplit[2];
        } else {
            this.opponent = null;
        }
    }

    public String getOpponent() {
        return this.opponent;
    }

    @Override
    protected String getParametersString() {
        return this.opponent;
    }
}
