package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class LoginMessage extends AccountingMessage {

    public LoginMessage(final int senderId, final String login, final String password) {
        super(Command.LOGIN, senderId, login, password);
    }

    /* package */ LoginMessage(final String[] messageSplit) {
        super(Command.LOGIN, messageSplit);
    }
}
