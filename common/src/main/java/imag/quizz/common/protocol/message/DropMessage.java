package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class DropMessage extends Message {

    private final String userLogin;
    private final long gameId;

    public DropMessage(final String[] messageSplit, final int senderId, final String userLogin, final long gameId) {
        super(Command.DROP, senderId);
        this.userLogin = userLogin;
        this.gameId = gameId;
    }

    public DropMessage(String[] messageSplit) {
        super(Command.DROP, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.userLogin = messageSplit[2];
        this.gameId = Long.parseLong(messageSplit[3]);
    }

    public long getGameId() {
        return gameId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    @Override
    protected String getParametersString() {
        return this.userLogin + Separator.LEVEL_1S + this.gameId;
    }
}
