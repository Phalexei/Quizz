package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

public class NokMessage extends Message {

    private final String errorMessage;

    public NokMessage() {
        super(Command.NOK);
        this.errorMessage = Long.toString(System.nanoTime());
    }

    /* package*/ NokMessage(final String messageString) {
        super(Command.NOK);
        final String[] split = messageString.split(Separator.LEVEL_1S);
        Validate.isTrue(this.command.name().equals(split[0]), "Invalid NOK message: '" + messageString + '\'');
        this.errorMessage = split[1];
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    protected String getParametersString() {
        return this.errorMessage;
    }
}
