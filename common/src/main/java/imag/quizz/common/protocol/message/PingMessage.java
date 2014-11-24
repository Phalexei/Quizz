package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class PingMessage extends Message {

    private final String data;

    public PingMessage() {
        super(Command.PING);
        this.data = Long.toString(System.nanoTime());
    }

    /* package*/ PingMessage(final String[] messageSplit) {
        super(Command.PING);
        this.checkCommandName(messageSplit[0]);
        this.data = messageSplit[1];
    }

    public String getData() {
        return this.data;
    }

    @Override
    protected String getParametersString() {
        return this.data;
    }
}
