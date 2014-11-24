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

    /* package*/ NokMessage(final String[] messageSplit) {
        super(Command.NOK);
        this.checkCommandName(messageSplit[0]);
        this.errorMessage = messageSplit[1];
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    protected String getParametersString() {
        return this.errorMessage;
    }
}
