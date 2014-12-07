package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import org.apache.commons.lang3.Validate;

public class PongMessage extends Message {

    private final String data;

    public PongMessage(final long senderId, final Message ping) {
        super(Command.PONG, senderId);
        Validate.isInstanceOf(PingMessage.class, ping, "Argument isn't a PingMessage");
        this.data = ((PingMessage) ping).getData();
    }

    /* package */ PongMessage(final String[] messageSplit) {
        super(Command.PONG, Long.parseLong(messageSplit[1]));
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
