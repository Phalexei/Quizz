package imag.quizz.client;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.protocol.message.Message;

public class ClientMessageHandler extends MessageHandler {

    protected ClientMessageHandler() {
        super("ClientMessageHandler");
    }

    @Override
    public void handleMessage(final int theInt, final Message message) {
        switch (message.getCommand()) {
            //TODO: fill in each case
            case PING:
                break;
            case PONG:
                System.out.println("PONG RECEIVED !");
                break;
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
                // TODO KEK
        }
    }
}
