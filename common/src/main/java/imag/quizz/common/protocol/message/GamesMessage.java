package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class GamesMessage extends Message {

    private final String gamesData;

    public GamesMessage(final long senderId, final String gamesData) {
        super(Command.GAMES, senderId);
        this.gamesData = gamesData;
    }

    /* package */ GamesMessage(final String[] messageSplit) {
        super(Command.GAMES, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        final StringBuilder builder = new StringBuilder();
        for (int i = 2; i < messageSplit.length; i++) {
            builder.append(messageSplit[i]);
            if (i != messageSplit.length - 1) {
                builder.append(Separator.LEVEL_1);
            }
        }
        this.gamesData = builder.toString();
    }

    @Override
    protected String getParametersString() {
        return this.gamesData;
    }
}
