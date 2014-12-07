package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class LogoutMessage extends Message {

    private final String playerLogin;

    public LogoutMessage(final long senderId, final String playerLogin) {
        super(Command.LOGOUT, senderId);
        this.playerLogin = playerLogin;
    }

    /* package */ LogoutMessage(final String[] messageSplit) {
        super(Command.LOGOUT, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        this.playerLogin = messageSplit[2];
    }

    public String getPlayerLogin() {
        return this.playerLogin;
    }

    @Override
    protected String getParametersString() {
        return this.playerLogin;
    }
}
