package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

import java.util.Arrays;

public class PlayMessage extends Message {

    private final long gameId;

    public PlayMessage(final long sourceId, final long gameId) {
        super(Command.PLAY, sourceId);
        this.gameId = gameId;
    }

    /* package */ PlayMessage(final String[] messageSplit) {
        super(Command.PLAY, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        try {
            this.gameId = Long.parseLong(messageSplit[2]);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Invalid PLAY message: " + Arrays.toString(messageSplit));
        }
    }

    public long getGameId() {
        return this.gameId;
    }

    @Override
    protected String getParametersString() {
        return Long.toString(this.gameId);
    }
}
