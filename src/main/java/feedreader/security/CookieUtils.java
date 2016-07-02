package feedreader.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.Md5Crypt;

import feedreader.entities.UserData;

public final class CookieUtils {

    public static void wipe(HttpServletResponse res, String cookieName) {
        Cookie c = new Cookie(cookieName, null);
        c.setMaxAge(0);
        res.addCookie(c);
    }

    public static String asString(String cookieName, Cookie[] cookies, String defValue) {
        Cookie cookie = findCookie(cookieName, cookies);
        String ret = defValue;
        if (cookie != null) {
            ret = cookie.getValue();
        }
        return ret;
    }

    private static Cookie findCookie(String cookieName, Cookie[] cookies) {
        Cookie ret = null;
        for (Cookie c : cookies) {
            if (c.getName().equals(cookieName)) {
                ret = c;
                break;
            }
        }
        return ret;
    }

    /**
     * Creates a new md5 salted hash based from user id and user email.
     */
    public static String generate(UserData userData) {
        String raw = Long.toString(userData.getUserId()) + "" + userData.getEmail();
        return Md5Crypt.md5Crypt(raw.getBytes());
    }

}
