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
        final String[] messageSplit = messageString.split(Separator.LEVEL_1S);
        final String commandString = messageSplit[0];
        final Command command;
        try {
            command = Command.valueOf(commandString);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid message: Invalid Command: '" + commandString + "'", e);
        }
        switch (command) {
            case PING:
                return new PingMessage(messageSplit);
            case PONG:
                return new PongMessage(messageSplit);
            case OK:
                return new OkMessage(messageSplit);
            case NOK:
                return new NokMessage(messageSplit);
            case INIT:
                break; // TODO Hard
            case REGISTER:
                return new RegisterMessage(messageSplit);
            case LOGIN:
                return new LoginMessage(messageSplit);
            case GAMES:
                break; // TODO Hard
            case NEW:
                return new NewMessage(messageSplit);
            case GAME:
                break; // TODO Hard
            case PLAY:
                return new PlayMessage(messageSplit);
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

    protected void checkCommandName(final String commandName) {
        Validate.isTrue(this.command.name().equals(commandName), "Invalid " + this.command.name() + " message");
    }

    protected abstract String getParametersString();

    public final String toString() {
        final String parameters = this.getParametersString();
        return this.command.name() + (parameters == null ? "" : Separator.LEVEL_1 + parameters);
    }
}
