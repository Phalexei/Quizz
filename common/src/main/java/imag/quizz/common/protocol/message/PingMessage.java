package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

public class PingMessage extends Message {

    private final String data;

    public PingMessage() {
        super(Command.PING);
        this.data = Long.toString(System.nanoTime());
    }

    /* package*/ PingMessage(final String messageString) {
        super(Command.PING);
        final String[] split = messageString.split(Separator.LEVEL_1S);
        Validate.isTrue(this.command.name().equals(split[0]), "Invalid PING message: '" + messageString + '\'');
        this.data = split[1];
    }

    public String getData() {
        return this.data;
    }

    @Override
    protected String getParametersString() {
        return this.data;
    }
}
