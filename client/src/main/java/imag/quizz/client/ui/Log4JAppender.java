package imag.quizz.client.ui;

import imag.quizz.common.tool.Log.Appender;
import org.apache.log4j.Level;

/**
 * Created by Ribesg.
 */
public class Log4JAppender extends Appender {

    private final Window window;

    protected Log4JAppender(final Window window) {
        super(Level.ALL /* TODO ChangeMe */);
        this.window = window;
    }

    @Override
    public void log(final String message) {
        this.window.log(message + '\n');
    }
}
