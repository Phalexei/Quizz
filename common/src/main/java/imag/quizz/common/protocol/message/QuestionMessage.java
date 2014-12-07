package imag.quizz.common.protocol.message;

import imag.quizz.common.protocol.Command;
import imag.quizz.common.protocol.Separator;
import org.apache.commons.lang3.Validate;

public class QuestionMessage extends Message {

    private final String question;

    private final String[] answers;

    public QuestionMessage(final long senderId, final String question, final String[] answers) {
        super(Command.QUESTION, senderId);
        Validate.isTrue(answers.length == 4);
        this.question = question;
        this.answers = answers;
    }


    /* package */ QuestionMessage(final String[] messageSplit) {
        super(Command.QUESTION, Long.parseLong(messageSplit[1]));
        this.checkCommandName(messageSplit[0]);
        this.question = messageSplit[2];

        this.answers = new String[4];
        System.arraycopy(messageSplit, 2, this.answers, 0, 4);
    }

    @Override
    protected String getParametersString() {
        final StringBuilder s = new StringBuilder();

        for (int i = 0; i < this.answers.length - 1; i++) {
            s.append(this.answers[i]);
            s.append(Separator.LEVEL_1);
        }
        s.append(this.answers[this.answers.length - 1]);

        return s.toString();
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }
}
