package feedreader.security;

import feedreader.config.Constants;
import feedreader.config.Environment;
import feedreader.log.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Admin {
    
    public static boolean isAdminUsername(String usrName) {
        return (usrName.equals("steven@feedrdr.co"));
    }

    public static boolean isValidAdmin(HttpSession session) {
        return Session.asString(session, Constants.SESSION_ADMIN_FIELD, "").equals(Constants.ADMIN_USERNAME);
    }

    public static boolean authenticate(HttpServletRequest req) {
        String usr = Parameter.asString(req, "username", "");
        String pwd = Parameter.asString(req, "password", "");

        // UserData userData = UsersTable.authenticate(usr, pwd);
        //
        // if (userData == null) {
        // return false;
        // }

        if (!usr.equals(Constants.ADMIN_USERNAME) || !pwd.equals("isadmin001")) {
            return false;
        }

        if (Environment.isDev()) {
            Logger.debug(UserSession.class).log("admin authenticated ").log(usr).end();
        }

        req.getSession().setAttribute(Constants.SESSION_ADMIN_FIELD, usr);

        return true;
    }

}
