package imag.quizz.common.tool;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    // ###################### //
    // ## Custom appenders ## //
    // ###################### //

    /**
     * Create a new Appender by extending this abstract class
     */
    public static abstract class Appender {

        private final Priority priority;

        protected Appender(final Priority priority) {
            this.priority = priority;
            Log.registerAppender(this);
        }

        public final Priority getPriority() {
            return this.priority;
        }

        public abstract void log(final String message);
    }

    private static final Map<Appender, Priority> customAppenders = new HashMap<>();

    private static void registerAppender(final Appender appender) {
        Log.customAppenders.put(appender, appender.getPriority());
    }

    private static void logToCustomAppenders(final Priority level, final String message, final Throwable t) {
        for (final Entry<Appender, Priority> e : Log.customAppenders.entrySet()) {
            if (e.getValue().isGreaterOrEqual(level)) {
                final String stacktrace;
                if (t != null) {
                    stacktrace = ExceptionUtils.getStackTrace(t);
                } else {
                    stacktrace = null;
                }
                e.getKey().log(message + (stacktrace == null ? "" : '\n' + stacktrace));
            }
        }
    }

    // ##################### //
    // ## Logging methods ## //
    // ##################### //

    public static boolean isEnabledFor(final Priority level) {
        return Log.logger.isEnabledFor(level);
    }

    public static void log(final Priority level, final String message) {
        Log.logger.log(level, message);
        Log.logToCustomAppenders(level, message, null);
    }

    public static void log(final Priority level, final String message, final Throwable t) {
        Log.logger.log(level, message, t);
        Log.logToCustomAppenders(level, message, t);
    }

    public static void trace(final String message) {
        logger.trace(message);
        Log.logToCustomAppenders(Level.TRACE, message, null);
    }

    public static void trace(final String message, final Throwable t) {
        logger.trace(message, t);
        Log.logToCustomAppenders(Level.TRACE, message, t);
    }

    public static void debug(final String message, final Throwable t) {
        Log.logger.debug(message, t);
        Log.logToCustomAppenders(Level.DEBUG, message, t);
    }

    public static void debug(final String message) {
        Log.logger.debug(message);
        Log.logToCustomAppenders(Level.DEBUG, message, null);
    }

    public static void info(final String message, final Throwable t) {
        Log.logger.info(message, t);
        Log.logToCustomAppenders(Level.INFO, message, t);
    }

    public static void info(final String message) {
        Log.logger.info(message);
        Log.logToCustomAppenders(Level.INFO, message, null);
    }

    public static void warn(final String message) {
        Log.logger.warn(message);
        Log.logToCustomAppenders(Level.WARN, message, null);
    }

    public static void warn(final String message, final Throwable t) {
        Log.logger.warn(message, t);
        Log.logToCustomAppenders(Level.WARN, message, t);
    }

    public static void error(final String message, final Throwable t) {
        Log.logger.error(message, t);
        Log.logToCustomAppenders(Level.ERROR, message, t);
    }

    public static void error(final String message) {
        Log.logger.error(message);
        Log.logToCustomAppenders(Level.ERROR, message, null);
    }

    public static void fatal(final String message) {
        Log.logger.fatal(message);
        Log.logToCustomAppenders(Level.FATAL, message, null);
    }

    public static void fatal(final String message, final Throwable t) {
        Log.logger.fatal(message, t);
        Log.logToCustomAppenders(Level.FATAL, message, t);
    }

    public static void assertLog(final boolean assertion, final String message) {
        Log.logger.assertLog(assertion, message);
        Log.logToCustomAppenders(Level.ERROR, message, null);
    }
}
