package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

// TODO
public class GamesMessage extends Message {
    public GamesMessage(String[] messageSplit) {
        super(Command.GAMES, Integer.parseInt(messageSplit[1]));
        //TODO: autogenerated stub
    }

    @Override
    protected String getParametersString() {
        return null;  //TODO: autogenerated stub
    }
}
