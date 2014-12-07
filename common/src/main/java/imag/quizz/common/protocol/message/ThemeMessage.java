package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class ThemeMessage extends Message {

    private final long playerId;
    private final long gameId;
    private final int chosenTheme;

    public ThemeMessage(final long senderId, final long gameId, final long playerId, final int chosenTheme) {
        super(Command.THEME, senderId);
        this.gameId = gameId;
        this.playerId = playerId;
        this.chosenTheme = chosenTheme;
    }

    /* package */ ThemeMessage(String[] messageSplit) {
        super(Command.THEME, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        gameId = Long.parseLong(messageSplit[2]);
        playerId = Long.parseLong(messageSplit[3]);
        chosenTheme = Integer.parseInt(messageSplit[4]);
    }

    @Override
    protected String getParametersString() {
        return gameId + Separator.LEVEL_1 + playerId + Separator.LEVEL_1 + chosenTheme;
    }
}
