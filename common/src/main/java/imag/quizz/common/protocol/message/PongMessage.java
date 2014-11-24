package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

public class PongMessage extends Message {

    private final String data;

    public PongMessage(final PingMessage ping) {
        super(Command.PONG);
        this.data = ping.getData();
    }

    /* package*/ PongMessage(final String messageString) {
        super(Command.PONG);
        final String[] split = messageString.split(Separator.LEVEL_1S);
        Validate.isTrue(this.command.name().equals(split[0]), "Invalid PONG message: '" + messageString + '\'');
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
