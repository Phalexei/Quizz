package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class LoginMessage extends AccountingMessage {

    protected LoginMessage(final String login, final String password) {
        super(Command.LOGIN, login, password);
    }

    /* package */ LoginMessage(final String messageString) {
        super(Command.LOGIN, messageString);
    }
}
