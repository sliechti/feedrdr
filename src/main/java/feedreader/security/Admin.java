package feedreader.security;

import javax.servlet.http.HttpSession;

import feedreader.config.Constants;

public class Admin {

    public static boolean isAdminUsername(String usrName) {
        return (usrName.equals("steven@feedrdr.co"));
    }

    public static boolean isValidAdmin(HttpSession session) {
        return Session.asString(session, Constants.SESSION_ADMIN_FIELD, "").equals(Constants.ADMIN_USERNAME);
    }

}
