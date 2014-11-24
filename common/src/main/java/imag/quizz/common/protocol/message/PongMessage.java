package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class PongMessage extends Message {

    private final String data;

    public PongMessage(final PingMessage ping) {
        super(Command.PONG);
        this.data = ping.getData();
    }

    /* package*/ PongMessage(final String[] messageSplit) {
        super(Command.PONG);
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
