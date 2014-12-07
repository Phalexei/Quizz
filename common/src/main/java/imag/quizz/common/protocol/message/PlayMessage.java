package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

import java.util.Arrays;

public class PlayMessage extends Message {

    private final long id;

    public PlayMessage(final long senderId, final long id) {
        super(Command.PLAY, senderId);
        this.id = id;
    }

    /* package */ PlayMessage(final String[] messageSplit) {
        super(Command.PLAY, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        try {
            this.id = Long.parseLong(messageSplit[2]);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Invalid PLAY message: " + Arrays.toString(messageSplit));
        }
    }

    public long getId() {
        return this.id;
    }

    @Override
    protected String getParametersString() {
        return Long.toString(this.id);
    }
}
