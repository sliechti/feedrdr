package feedreader.time;

public class CurrentTime {

    public static final int SECONDS_PER_DAY = 86400;
    public static final long MILLIS_PER_DAY = SECONDS_PER_DAY * 1000;

    public static long inGMT() {
        return System.currentTimeMillis();
    }
}
