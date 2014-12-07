package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class ThemeMessage extends Message {

    private final long gameId;
    private final int  chosenTheme;

    public ThemeMessage(final long senderId, final long gameId, final int chosenTheme) {
        super(Command.THEME, senderId);
        this.gameId = gameId;
        this.chosenTheme = chosenTheme;
    }

    /* package */ ThemeMessage(final String[] messageSplit) {
        super(Command.THEME, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.gameId = Long.parseLong(messageSplit[2]);
        this.chosenTheme = Integer.parseInt(messageSplit[3]);
    }

    public long getGameId() {
        return this.gameId;
    }

    public int getChosenTheme() {
        return this.chosenTheme;
    }

    @Override
    protected String getParametersString() {
        return this.gameId + Separator.LEVEL_1 + this.chosenTheme;
    }
}
