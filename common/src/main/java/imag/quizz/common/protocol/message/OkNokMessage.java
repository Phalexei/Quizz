package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;

import java.util.Arrays;

public abstract class OkNokMessage extends Message {

    private final String  text;

    private final Message source;

    protected OkNokMessage(final Command command, final long senderId, final String text, final Message source) {
        super(command, senderId);
        this.text = text;
        this.source = source;
    }

    protected OkNokMessage(final Command command, final String[] messageSplit) {
        super(command, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.text = messageSplit[2];
        final String[] originalMessageSplit = Arrays.copyOfRange(messageSplit, 3, messageSplit.length);
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalMessageSplit.length; i++) {
            builder.append(originalMessageSplit[i]);
            if (i != originalMessageSplit.length - 1) {
                builder.append(Separator.LEVEL_1);
            }
        }
        this.source = Message.fromString(builder.toString());
    }

    public Message getSource() {
        return source;
    }

    public String getText() {
        return text;
    }

    @Override
    protected String getParametersString() {
        final String originalMessageString = this.source.toString();
        return this.text + Separator.LEVEL_1 + originalMessageString.substring(0, originalMessageString.length() - 1);
    }
}
