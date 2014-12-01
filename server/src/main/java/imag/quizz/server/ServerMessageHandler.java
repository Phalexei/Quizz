package imag.quizz.server;

import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.protocol.message.Message;
import imag.quizz.common.protocol.message.PingMessage;
import imag.quizz.common.protocol.message.PongMessage;

public class ServerMessageHandler extends MessageHandler {

    public ServerMessageHandler() {
    }

    @Override
    public void handleMessage(int port, Message message) {
        System.out.println("Server handling message : " + message.toString() + " from port : " + port);

        switch (message.getCommand()) {
            //TODO: fill in each case
            case PING:
                send(port, new PongMessage((PingMessage) message));
                break;
            case PONG:
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
        }
    }
}
