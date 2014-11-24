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

    /* package*/ AccountingMessage(final Command command, final String[] messageSplit) {
        super(command);
        this.checkCommandName(messageSplit[0]);
        this.login = messageSplit[1];
        this.hashedPassword = messageSplit[2];
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