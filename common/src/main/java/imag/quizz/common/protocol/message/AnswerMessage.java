package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;

public class AnswerMessage extends Message {

    private final int chosenAnswer;

    protected AnswerMessage(final int senderId, final int chosenAnswer) {
        super(Command.ANSWER, senderId);
        this.chosenAnswer = chosenAnswer;
    }

    /* package */ AnswerMessage(final String[] messageSplit) {
        super(Command.ANSWER, Integer.parseInt(messageSplit[1]));
        this.chosenAnswer = Integer.parseInt(messageSplit[2]);
    }

    public int getChosenAnswer() {
        return this.chosenAnswer;
    }

    @Override
    protected String getParametersString() {
        return Integer.toString(this.chosenAnswer);
    }
}
