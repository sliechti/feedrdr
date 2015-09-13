package feedreader.utils;

public class SQLUtils {

    public static String asSafeString(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.replaceAll("'", "''"); // TODO: Do better.
    }
    
}
