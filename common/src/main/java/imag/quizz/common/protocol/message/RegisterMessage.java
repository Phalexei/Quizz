package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class RegisterMessage extends AccountingMessage {

    protected RegisterMessage(final String login, final String password) {
        super(Command.REGISTER, login, password);
    }

    /* package */ RegisterMessage(final String[] messageSplit) {
        super(Command.REGISTER, messageSplit);
    }
}
