package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class GameMessage extends Message {

    private final String gameData;

    public GameMessage(final long senderId, final String gameData) {
        super(Command.GAME, senderId);
        this.gameData = gameData;
    }

    /* package */ GameMessage(final String[] messageSplit) {
        super(Command.GAME, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        final StringBuilder builder = new StringBuilder();
        for (int i = 2; i < messageSplit.length; i++) {
            builder.append(messageSplit[i]);
            if (i != messageSplit.length - 1) {
                builder.append(Separator.LEVEL_1);
            }
        }
        this.gameData = builder.toString();
    }

    public String getGameData() {
        return this.gameData;
    }

    @Override
    protected String getParametersString() {
        return this.gameData;
    }
}
