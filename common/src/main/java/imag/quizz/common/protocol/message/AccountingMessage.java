package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.tool.Hash;

public abstract class AccountingMessage extends Message {

    private final String login;
    private final String hashedPassword;

    protected AccountingMessage(final Command command, final String login, final String password) {
        super(command);
        this.login = login;
        this.hashedPassword = Hash.md5(password);
    }

    /* package*/ AccountingMessage(final Command command, final String messageString) {
        super(command);
        final String[] split = messageString.split(Separator.LEVEL_1S);
        this.checkCommandName(split[0]);
        this.login = split[1];
        this.hashedPassword = split[2];
    }

    public String getLogin() {
        return this.login;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @Override
    protected String getParametersString() {
        return this.login + Separator.LEVEL_1 + this.hashedPassword;
    }
}
