package imag.quizz.common.tool;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Logging class.
 * TODO Javadocs
 *
 * @author Ribesg
 */
public class Log {

    // ################# //
    // ## Back Logger ## //
    // ################# //

    private static final Logger logger = Logger.getLogger("Quizz");

    public static Logger get() {
        return Log.logger;
    }

    // ##################### //
    // ## Logging methods ## //
    // ##################### //

    public static boolean isEnabledFor(final Priority level) {
        return Log.logger.isEnabledFor(level);
    }

    public static void log(final Priority level, final String message) {
        Log.logger.log(level, message);
    }

    public static void log(final Priority level, final String message, final Throwable t) {
        Log.logger.log(level, message, t);
    }

    public static void trace(final String message) {
        logger.trace(message);
    }

    public static void trace(final String message, final Throwable t) {
        logger.trace(message, t);
    }

    public static void debug(final String message, final Throwable t) {
        Log.logger.debug(message, t);
    }

    public static void debug(final String message) {
        Log.logger.debug(message);
    }

    public static void info(final String message, final Throwable t) {
        Log.logger.info(message, t);
    }

    public static void info(final String message) {
        Log.logger.info(message);
    }

    public static void warn(final String message) {
        Log.logger.warn(message);
    }

    public static void warn(final String message, final Throwable t) {
        Log.logger.warn(message, t);
    }

    public static void error(final String message, final Throwable t) {
        Log.logger.error(message, t);
    }

    public static void error(final String message) {
        Log.logger.error(message);
    }

    public static void fatal(final String message) {
        Log.logger.fatal(message);
    }

    public static void fatal(final String message, final Throwable t) {
        Log.logger.fatal(message, t);
    }

    public static void assertLog(boolean assertion, final String message) {
        Log.logger.assertLog(assertion, message);
    }
}
