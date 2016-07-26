package feedreader.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;

public class Session {

    public static final int INVALID_USER_ID = 0;

    public static boolean asBoolean(HttpSession session, String attrName, boolean defaultValue) {
        Object b = session.getAttribute(attrName);

        if (b instanceof Boolean) {
            return (Boolean) b;
        }

        return defaultValue;
    }

    public static int asInt(HttpSession session, String attrName, int i) {
        return (int) asLong(session, attrName, i);
    }

    public static long asLong(HttpSession session, String attrName, long defaultValue) {
        Object l = session.getAttribute(attrName);

        if (l instanceof Long)
            return (long) l;

        if (l instanceof Integer)
            return (int) l;

        if (l instanceof Short)
            return (short) l;

        return defaultValue;
    }

    public static String asString(HttpSession session, String attrName, String defaultValue) {
        Object str = session.getAttribute(attrName);

        if (str instanceof String) {
            return (String) str;
        }

        if (str instanceof Long) {
            return Long.toString((Long) str);
        }

        return defaultValue;
    }

    public static long getProfileId(HttpSession session) {
        return Session.asLong(session, Constants.SESSION_SELECTED_PROFILE_ID, INVALID_USER_ID);
    }

    public static long getUserId(HttpSession session) {
        return Session.asLong(session, Constants.SESSION_USERID_FIELD, INVALID_USER_ID);
    }

    public static int getUserType(HttpSession session) {
        return Session.asInt(session, Constants.SESSION_USER_TYPE, FeedAppConfig.DEFAULT_USER_VAL);
    }

    public static void invalidate(HttpServletRequest req) {
        HttpSession sess = req.getSession(false);
        if (sess != null) {
            sess.invalidate();
        }
    }

    public static void set(HttpSession session, String name, Object value) {
        session.setAttribute(name, value);
    }
}
