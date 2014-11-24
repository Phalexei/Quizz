package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

/**
 *
 */
public abstract class Message {

    public static Message fromString(final String messageString) {
        Validate.notEmpty(messageString, "messageString should not be null nor empty");
        final String commandString = messageString.split(Separator.LEVEL_1S)[0];
        final Command command;
        try {
            command = Command.valueOf(commandString);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid message: Invalid Command: '" + commandString + "'", e);
        }
        switch (command) {
            case PING:
                return new PingMessage(messageString);
            case PONG:
                return new PongMessage(messageString);
            case OK:
                break;
            case NOK:
                break;
            case INIT:
                break;
            case REGISTER:
                break;
            case LOGIN:
                break;
            case GAMES:
                break;
            case NEW:
                break;
            case GAME:
                break;
            case PLAY:
                break;
            case THEMES:
                break;
            case THEME:
                break;
            case QUESTION:
                break;
            case ANSWER:
                break;
            case NOANSWER:
                break;
            case WAIT:
                break;
            case DROP:
                break;
            case END:
                break;
            default:
                throw new UnsupportedOperationException("Missing handler for Command '" + commandString + '\'');
        }
        throw new UnsupportedOperationException("Not implemented yet: " + commandString); // TODO
    }

    protected final Command command;

    protected Message(final Command command) {
        this.command = command;
    }

    public final Command getCommand() {
        return this.command;
    }

    protected abstract String getParametersString();

    public final String toString() {
        return this.command.name() + Separator.LEVEL_1 + this.getParametersString();
    }
}
