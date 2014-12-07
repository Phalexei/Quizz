package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class LogoutMessage extends Message {

    private final long playerId;

    public LogoutMessage(final long senderId, final long playerId) {
        super(Command.LOGOUT, senderId);
        this.playerId = playerId;
    }

    /* package */ LogoutMessage(String[] messageSplit) {
        super(Command.LOGOUT, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        this.playerId = Long.parseLong(messageSplit[2]);
    }

    @Override
    protected String getParametersString() {
        return Long.toString(playerId);
    }
}
