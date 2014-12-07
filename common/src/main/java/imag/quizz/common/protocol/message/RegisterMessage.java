package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class RegisterMessage extends AccountingMessage {

    public RegisterMessage(final long sourceId, final String login, final String password) {
        super(Command.REGISTER, sourceId, login, password);
    }

    /* package */ RegisterMessage(final String[] messageSplit) {
        super(Command.REGISTER, messageSplit);
    }
}
