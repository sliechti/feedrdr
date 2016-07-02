package feedreader.log;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import feedreader.config.ConfigSystem;
import feedreader.time.ConfigDate;
import feedreader.time.CurrentTime;

public class LogRoot {

    private Logger.LogLevels logLevel = Logger.LogLevels.DEBUG;

    public enum NewLineMode {
        UNIX, WINDOWS, HTML
    };

    private NewLineMode nlMode = NewLineMode.WINDOWS;

    private final LogNode node = new LogNode();
    private final EmptyNode emptyNode = new EmptyNode();
    private Writer out;

    private static final SimpleDateFormat fmt = new SimpleDateFormat("-- dd'/'hh:mm:ss.SSS z");

    boolean ended = true;
    boolean debugSql = false;

    static {
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    LogRoot(Writer out, Logger.LogLevels level) {
        try {
            out.append("LogLevel set ").append(level.name()).append(" logging with ")
                            .append(out.getClass().getSimpleName()).append(ConfigSystem.getEol());
        } catch (IOException e) {
        }
        this.logLevel = level;
        this.out = out;
    }

    public void setWriter(Writer fileWriter) {
        out = fileWriter;
    }
    
    public void setLevel(Logger.LogLevels level) throws IOException {
        this.logLevel = level;
        out.append("LogLevel set ").append(level.name()).append(ConfigSystem.getEol());
    }

    public void setErrorLevel() throws IOException {
        this.logLevel = Logger.LogLevels.ERROR;
        out.append("LogLevel set ").append(this.logLevel.name()).append(ConfigSystem.getEol());
    }

    public void setDebugSql(boolean b) {
        debugSql = b;
    }

    private void write(String s) {
        write(s, false);
    }

    private void write(String s, boolean flush) {
        try {
            out.write(s);
            if (flush) {
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ILog {

        public ILog log(long l);

        public ILog log(CharSequence s);

        public ILog log(Object o);

        public ILog log(Class<?> clazz);

        public ILog log(Date date);

        public void end();
    }

    private class LogNode implements ILog {

        @Override
        public ILog log(long l) {
            write(String.valueOf(l));
            return this;
        }

        @Override
        public ILog log(CharSequence s) {
            write(s.toString());
            return this;
        }

        @Override
        public ILog log(Class<?> clazz) {
            write(clazz.getSimpleName());
            write(".class");
            return this;
        }

        @Override
        public ILog log(Object o) {
            write(o.toString());
            return this;
        }

        @Override
        public ILog log(Date date) {
            write(ConfigDate.def.format(date));
            return this;
        }

        @Override
        public void end() {
            switch (nlMode) {
            case HTML:
                write("<br>", true);
                break;
            case UNIX:
                write("\n", true);
                break;
            case WINDOWS:
                write("\r\n", true);
                break;
            }

            ended = true;
        }
    }

    private class EmptyNode implements ILog {

        @Override
        public ILog log(long l) {
            return this;
        };

        @Override
        public ILog log(CharSequence s) {
            return this;
        };

        @Override
        public ILog log(Object o) {
            return this;
        };

        @Override
        public ILog log(Class<?> clazz) {
            return this;
        };

        @Override
        public ILog log(Date date) {
            return this;
        };

        @Override
        public void end() {
            ended = true;
        };
    }

    public void setNewLine(NewLineMode mode) {
        this.nlMode = mode;
    }

    public ILog start(Logger.LogLevels lvl) {
        if (debugSql && lvl == Logger.LogLevels.DEBUG_SQL) {
        } else if (lvl.getVal() < logLevel.getVal()) {
            return emptyNode;
        }

        if (!ended) {
            node.end();
            node.log(Logger.LogLevels.WARNING.asString());
            node.log(" *** Last log entry wasn't end()ed ***");
            node.end();
        }

        write(fmt.format(new Date(CurrentTime.inGMT())));
        write(" ");
        write(lvl.asString());
        write(":");

        ended = false;
        return node;
    }

}
