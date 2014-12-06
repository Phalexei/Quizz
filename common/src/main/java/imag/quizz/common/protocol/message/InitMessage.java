package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

public class InitMessage extends Message {

    private String data;

    public InitMessage(final int senderId, final String data) {
        super(Command.INIT, senderId);
        this.data = data;
    }

    /* package */ InitMessage(final String[] messageSplit) {
        super(Command.INIT, Integer.parseInt(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);

        final StringBuilder builder = new StringBuilder();
        for (int i = 2; i < messageSplit.length; i++) {
            builder.append(messageSplit[i]);
            if (i != messageSplit.length - 1) {
                builder.append(Separator.LEVEL_1);
            }
        }
        this.data = builder.toString();
    }

    public String getData() {
        return this.data;
    }

    @Override
    protected String getParametersString() {
        return this.data;
    }
}
