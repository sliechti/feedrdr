package feedreader.config;

public class ConfigSystem {

    public enum NewLineMode {
        UNIX("\n"), WINDOWS("\r\n"), HTML("<br>"), NONE("");

        private String eol;

        NewLineMode(String eol) {
            this.eol = eol;
        }

        public String getEol() {
            return eol;
        }
    };

    private static NewLineMode systemEol;

    static {
        String eol = System.getProperty("system.eol");
        if (eol != null) {
            if (eol.equalsIgnoreCase("none")) {
                systemEol = NewLineMode.NONE;
            }
        }
    }

    public static void setEol(NewLineMode mode) {
        systemEol = mode;
    }

    public static String getEol() {
        if (systemEol != null)
            return systemEol.getEol();

        String line = System.getProperty("line.separator");

        if (line == null)
            return NewLineMode.UNIX.getEol();

        return line;
    }

    public boolean test() {
        // TODO - implement ConfigSystem.test
        throw new UnsupportedOperationException();
    }

}
