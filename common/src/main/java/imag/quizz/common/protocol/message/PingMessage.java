package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class PingMessage extends Message {

    private final String data;

    public PingMessage(final int senderId) {
        super(Command.PING, senderId);
        this.data = Long.toString(System.nanoTime());
    }

    /* package*/ PingMessage(final String[] messageSplit) {
        super(Command.PING, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.data = messageSplit[2];
    }

    public String getData() {
        return this.data;
    }

    @Override
    protected String getParametersString() {
        return this.data;
    }
}
