package feedreader.utils;

import feedreader.config.Environment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONUtils {

    // TODO: performance. Instantiation.
    public static String escapeQuotes(String str) {
        if (str == null || str.isEmpty())
            return "";
        str = str.replaceAll("\n", " ");
        str = str.replaceAll("\\\\", "\\\\\\\\");
        return str.replaceAll("\"", "'");
    }

    public static String getString(ResultSet rs, String name) {
        try {
            return "\"" + name + "\":\"" + escapeQuotes(rs.getString(name)) + "\"";
        } catch (SQLException ex) {
            return "\"" + name + "\":\"error\"";
        }
    }

    public static String getNumber(ResultSet rs, String name) {
        try {
            return "\"" + name + "\":" + rs.getLong(name) + "";
        } catch (SQLException ex) {
            return "\"" + name + "\":\"error\"";
        }
    }

    public static Object getBoolean(ResultSet rs, String name) {
        try {
            return "\"" + name + "\":" + rs.getBoolean(name) + "";
        } catch (SQLException ex) {
            return "\"" + name + "\":\"error\"";
        }
    }

    public static String msg(String msg) {
        return "{\"message\" : \"" + msg + "\"}";
    }

    public static String error(int code, String msg) {
        return "{\"error\" : \"" + escapeQuotes(msg) + "\", \"code\" : 0}";
    }

    public static String error(int code, String msg, Exception e) {
        if (Environment.isDev()) {
            return "{\"error\" : \"DEV : " + msg + ", error " + escapeQuotes(e.getMessage()) + "\", \"code\" : " + code
                    + "}";
        }

        return "{\"error\" : \"" + escapeQuotes(msg) + "\", \"code\" : " + code + "}";
    }

    public static String empty() {
        return "{}";
    }

    public static String success(String msg) {
        return "{\"success\" : \"" + msg + "\", \"ok\" : true}";
    }

    public static String success(String msg, String append) {
        return "{\"success\" : \"" + msg + "\", \"ok\" : true, " + append + "}";
    }

    public static String count(int count) {
        return "{\"count\" : " + count + "}";
    }

    public static String count(boolean count) {
        return "{\"count\" : " + ((count) ? 1 : 0) + "}";
    }

    public static String asString(String name, String value) {
        return "{\"" + name + "\" : \"" + value + "\"}";
    }

    public static String asNumber(String name, long value) {
        return "{\"" + name + "\" : " + value + "}";
    }

    public static String asBoolean(String name, boolean value) {
        return "{\"" + name + "\" : " + value + "}";
    }

    public static StringBuilder append(StringBuilder sb, String name, long value) {
        return sb.append("\"" + name + "\" : " + value);
    }

    public static StringBuilder append(StringBuilder sb, String name, String value) {
        return sb.append("\"" + name + "\" : " + value + "\"");
    }

    public static StringBuilder append(StringBuilder sb, String name, boolean value) {
        return sb.append("\"" + name + "\" : " + value);
    }

}
