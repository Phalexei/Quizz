package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import imag.quizz.common.tool.Hash;

public abstract class AccountingMessage extends Message {

    private final String login;
    private final String hashedPassword;

    protected AccountingMessage(final Command command, final long senderId, final String login, final String password) {
        super(command, senderId);
        this.login = login;
        this.hashedPassword = Hash.md5(password);
    }

    /* package */ AccountingMessage(final Command command, final String[] messageSplit) {
        super(command, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.login = messageSplit[2];
        this.hashedPassword = Hash.decodeBase64(messageSplit[3]);
    }

    public String getLogin() {
        return this.login;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @Override
    protected String getParametersString() {
        return this.login + Separator.LEVEL_1 + Hash.encodeBase64(this.hashedPassword);
    }
}
