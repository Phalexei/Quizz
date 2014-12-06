package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class NoAnswerMessage extends Message {
    public NoAnswerMessage(String[] messageSplit) {
        super(Command.NOANSWER, Integer.parseInt(messageSplit[1]));
        //TODO: autogenerated stub
    }

    @Override
    protected String getParametersString() {
        return null;  //TODO: autogenerated stub
    }
}
