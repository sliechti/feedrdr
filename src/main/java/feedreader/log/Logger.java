package feedreader.log;

import feedreader.config.FeedAppConfig;

import java.io.PrintWriter;

/**
 * TODO: Check out:
 *
 * http://www.diogonunes.com/it/work/jcdp/
 */
public class Logger {

    public enum LogLevels {
        DEBUG_SQL(-1),
        // Anything below 0 is asked to be logged explicitly.
        // E.g Logger.get().setDebugSql(true)
        DEBUG(0), NOTICE(1), INFO(2), WARNING(3), ERROR(4), CRITICAL(5), FATAL(6), NONE(7);

        int val = 0;

        LogLevels(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public String asString() {
            return this.toString();
        }

        public static Logger.LogLevels fromVal(int val) {
            switch (val) {
            case -1:
                return DEBUG_SQL;
            case 0:
                return DEBUG;
            case 1:
                return NOTICE;
            case 2:
                return INFO;
            case 3:
                return WARNING;
            case 4:
                return ERROR;
            case 5:
                return CRITICAL;
            case 6:
                return FATAL;
            case 7:
                return NONE;
            }

            return DEBUG;
        }
    }

    private static LogRoot root;

    static {
        root = new LogRoot(new PrintWriter(System.out, true), LogLevels.fromVal(FeedAppConfig.DEFAULT_LOG_LEVEL));
    }

    public static LogRoot get() {
        return root;
    }

    public static LogRoot.ILog debug(CharSequence s) {
        return root.start(LogLevels.DEBUG).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog debug(Class<?> clazz) {
        return root.start(LogLevels.DEBUG).log(" ").log(clazz).log(" ");
    }

    public static LogRoot.ILog debugSQL(CharSequence s) {
        return root.start(LogLevels.DEBUG_SQL).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog debugSQL(Class<?> clazz) {
        return root.start(LogLevels.DEBUG_SQL).log(" ").log(clazz).log(" ");
    }

    public static LogRoot.ILog error(CharSequence s) {
        return root.start(LogLevels.ERROR).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog error(Class<?> clazz) {
        return root.start(LogLevels.ERROR).log(" ").log(clazz).log(" ");
    }

    public static LogRoot.ILog warning(CharSequence s) {
        return root.start(LogLevels.WARNING).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog warning(Class<?> clazz) {
        return root.start(LogLevels.WARNING).log(" ").log(clazz).log(" ");
    }

    public static LogRoot.ILog info(CharSequence s) {
        return root.start(LogLevels.INFO).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog info(Class<?> clazz) {
        return root.start(LogLevels.INFO).log(" ").log(clazz).log(" ");
    }

    public static LogRoot.ILog notice(CharSequence s) {
        return root.start(LogLevels.NOTICE).log(" ").log(s).log(" ");
    }

    public static LogRoot.ILog notice(Class<?> clazz) {
        return root.start(LogLevels.NOTICE).log(" ").log(clazz).log(" ");
    }
}
