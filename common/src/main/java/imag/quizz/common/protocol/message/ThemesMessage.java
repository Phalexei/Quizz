package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

public class ThemesMessage extends Message {

    private final long     gameId;
    private final String[] themes;

    public ThemesMessage(final long senderId, final long gameId, final String[] themes) {
        super(Command.THEMES, senderId);
        Validate.isTrue(themes.length == 4);
        this.gameId = gameId;
        this.themes = themes;
    }

    /* package */ ThemesMessage(final String[] messageSplit) {
        super(Command.THEMES, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        this.gameId = Long.parseLong(messageSplit[2]);
        this.themes = new String[4];
        System.arraycopy(messageSplit, 3, this.themes, 0, 4);
    }

    @Override
    protected String getParametersString() {
        final StringBuilder s = new StringBuilder();

        for (int i = 0; i < this.themes.length - 1; i++) {
            s.append(this.themes[i]);
            s.append(Separator.LEVEL_1);
        }
        s.append(this.themes[this.themes.length - 1]);

        return s.toString();
    }

    public String[] getThemes() {
        return this.themes;
    }
}
