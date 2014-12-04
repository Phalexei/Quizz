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
        Message newMessage = null;
        switch (command) {
            case PING:
                newMessage = new PingMessage(messageSplit);
                break;
            case PONG:
                newMessage = new PongMessage(messageSplit);
                break;
            case OK:
                newMessage = new OkMessage(messageSplit);
                break;
            case NOK:
                newMessage = new NokMessage(messageSplit);
                break;
            case INIT:
                break; // TODO Hard
            case REGISTER:
                newMessage = new RegisterMessage(messageSplit);
                break;
            case LOGIN:
                newMessage = new LoginMessage(messageSplit);
                break;
            case GAMES:
                break; // TODO Hard
            case NEW:
                newMessage = new NewMessage(messageSplit);
                break;
            case GAME:
                break; // TODO Hard
            case PLAY:
                newMessage = new PlayMessage(messageSplit);
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

        if (newMessage == null) {
            throw new UnsupportedOperationException("Not implemented yet: " + commandString); // TODO
        }

        return newMessage;
    }

    protected final Command command;
    protected final int     senderId;

    protected Message(final Command command, final int senderId) {
        this.command = command;
        this.senderId = senderId;
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
        return this.command.name() + Separator.LEVEL_1 + this.senderId
                + (parameters == null ? "" : Separator.LEVEL_1 + parameters) + '\n';
    }
}
