package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NewMessage extends Message {

    private final String opponent;

    public NewMessage(final int senderId) {
        this(senderId, null);
    }

    public NewMessage(final int senderId, final String opponent) {
        super(Command.NEW, senderId);
        this.opponent = opponent;
    }

    /* package */ NewMessage(final String[] messageSplit) {
        super(Command.NEW, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.opponent = messageSplit[2];
    }

    public String getOpponent() {
        return this.opponent;
    }

    @Override
    protected String getParametersString() {
        return this.opponent;
    }
}
