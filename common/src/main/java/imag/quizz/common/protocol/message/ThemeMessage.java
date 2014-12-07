package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class ThemeMessage extends Message {

    private final int chosenTheme;

    public ThemeMessage(final long sourceId, final int chosenTheme) {
        super(Command.THEME, sourceId);
        this.chosenTheme = chosenTheme;
    }

    /* package */ ThemeMessage(final String[] messageSplit) {
        super(Command.THEME, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.chosenTheme = Integer.parseInt(messageSplit[2]);
    }

    public int getChosenTheme() {
        return this.chosenTheme;
    }

    @Override
    protected String getParametersString() {
        return Integer.toString(this.chosenTheme);
    }
}
