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
        final String[] messageSplit = messageString.split(Separator.LEVEL_1);
        final String commandString = messageSplit[0];
        final Command command;
        try {
            command = Command.valueOf(commandString);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid message: Invalid Command: '" + commandString + "'", e);
        }
        Message newMessage;
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
                newMessage = new InitMessage(messageSplit); // TODO Add Data
                break;
            case REGISTER:
                newMessage = new RegisterMessage(messageSplit);
                break;
            case LOGIN:
                newMessage = new LoginMessage(messageSplit);
                break;
            case GAMES:
                newMessage = new GamesMessage(messageSplit);
                break;
            case NEW:
                newMessage = new NewMessage(messageSplit);
                break;
            case GAME:
                newMessage = new GameMessage(messageSplit);
                break;
            case PLAY:
                newMessage = new PlayMessage(messageSplit);
                break;
            case THEMES:
                newMessage = new ThemesMessage(messageSplit);
                break;
            case THEME:
                newMessage = new ThemeMessage(messageSplit);
                break;
            case QUESTION:
                newMessage = new QuestionMessage(messageSplit);
                break;
            case ANSWER:
                newMessage = new AnswerMessage(messageSplit);
                break;
            case NOANSWER:
                newMessage = new NoAnswerMessage(messageSplit);
                break;
            case WAIT:
                newMessage = new WaitMessage(messageSplit);
                break;
            case DROP:
                newMessage = new DropMessage(messageSplit);
                break;
            case END:
                newMessage = new EndMessage(messageSplit);
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
    protected final long    senderId;

    protected Message(final Command command, final long senderId) {
        this.command = command;
        this.senderId = senderId;
    }

    public final Command getCommand() {
        return this.command;
    }

    public long getSenderId() {
        return this.senderId;
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
