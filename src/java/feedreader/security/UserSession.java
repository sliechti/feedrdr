package feedreader.security;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import feedreader.config.Constants;
import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.entities.ProfileData;
import feedreader.entities.UserData;
import feedreader.log.Logger;
import feedreader.store.UserProfilesTable;
import feedreader.store.UsersTable;
import feedreader.utils.PageUtils;
import feedreader.utils.Validate;

public class UserSession {

    public static final Class<?> clz = UserSession.class;

    public static boolean createNew(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String username = Parameter.asString(request, Constants.INPUT_SCREEN_NAME, "");
        String email = Parameter.asString(request, Constants.INPUT_EMAIL_NAME, "");
        String password = Parameter.asString(request, Constants.INPUT_PWD_NAME, "");
        String locale = request.getLocale().toString();

        if (locale.isEmpty()) {
            locale = FeedAppConfig.DEFAULT_LOCALE;
        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.sendRedirect(PageUtils.getPath("?error=0"));
        }

        if (!Validate.isValidEmailAddress(email)) {
            response.sendRedirect(PageUtils.getPath("?error=1"));
        }

        UserData usrData = UsersTable.createNewUser(email, username, password, locale);
        if (usrData.getUserId() > 0) {
            if (!UserSession.initUserSession(request, usrData)) {
                response.sendRedirect(PageUtils.getPath("?error=2"));
            } else {
                PageUtils.gotoHome(response);
                return true;
            }
        } else {
            response.sendRedirect(PageUtils.getPath("?error=3"));
        }

        return false;
    }

    public static boolean initUserSession(HttpServletRequest req, UserData userData) {
        HttpSession session = req.getSession();

        Session.set(session, Constants.SESSION_USERID_FIELD, userData.getUserId());
        Session.set(session, Constants.SESSION_USER_TYPE, userData.getUserType().val());
        Session.set(session, Constants.SESSION_USER_EMAIL_FIELD, userData.getEmail());
        Session.set(session, Constants.SESSION_USER_SCREEN_NAME, userData.getScreenName());
        Session.set(session, Constants.SESSION_EMAIL_VERIFIED, userData.isVerified());

        ProfileData profile;

        if (UserProfilesTable.getProfileCount(userData.getUserId()) == 0) {
            long profileId = UserProfilesTable.createDefaultProfile(userData.getUserId());
            if (profileId == 0) {
                return false;
            }
            profile = UserProfilesTable.getProfile(userData.getUserId(), profileId);
        } else {
            profile = UserProfilesTable.getProfile(userData.getUserId(), userData.getSelectedProfileId());
            if (profile == ProfileData.NULL) {
                profile = UserProfilesTable.getFirstProfile(userData.getUserId());
            }
        }

        Session.set(session, Constants.SESSION_SELECTED_PROFILE_ID, profile.getProfileId());
        Session.set(session, Constants.SESSION_SELECTED_PROFILE_NAME, profile.getName());
        Session.set(session, Constants.SESSION_SELECTED_PROFILE_COLOR, profile.getColor());

        if (Environment.isDev()) {
            Logger.debug(UserSession.class).log("initUserSession ").log(userData).end();
        }

        return true;
    }

    public static long authFromCookie(HttpServletRequest request) {
        String cookieKey = UserSession.authCookie(request);
        Logger.info(clz).log("authenticating from cookie " + cookieKey);
        if (cookieKey.isEmpty()) {
            return 0;
        }

        UserData userData = UsersTable.fromCookie(cookieKey);
        if (userData == null) {
            return -1;
        }

        if (!initUserSession(request, userData)) {
            return 0;
        }
        return userData.getUserId();
    }

    private static String authCookie(HttpServletRequest request) {
        return CookieUtils.asString(Constants.USER_COOKIE, request.getCookies(), "");
    }

    public static int authenticate(HttpServletRequest req, HttpServletResponse response) {
        
        String email = Parameter.asString(req, Constants.INPUT_EMAIL_NAME, "");
        String pwd = Parameter.asString(req, Constants.INPUT_PWD_NAME, "");
        boolean rememberMe = Parameter.asBoolean(req, Constants.INPUT_REMEMBER_ME, false);

        UserData userData = UsersTable.get(email, pwd);

        if (userData.getUserId() == 0) {
            return -1;
        }

        if (!initUserSession(req, userData)) {
            return 0;
        }

        if (rememberMe) {
            String cookieKey = CookieUtils.generate(userData);
            Cookie cookie = new Cookie(Constants.USER_COOKIE, cookieKey);
            cookie.setMaxAge(Constants.DEFAUT_COOKIE_AGE);
            response.addCookie(cookie);
            UsersTable.saveCookie(userData, cookieKey);
        }

        return 1;
    }

    /**
     * Tries to get the user id from the current session. Fall backs to the cookie {@link Constants#USER_COOKIE}.
     */
    public static long getUserId(HttpServletRequest req) {
        long ret = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        if (ret == 0 && req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if (c.getName().equals(Constants.USER_COOKIE)) {
                    try {
                        ret = authFromCookie(req);
                        break;
                    } catch (Exception e) {
                        Logger.error(clz).log("error parsing cookie value ").log(c.getValue()).end();
                    }
                }
            }
        }
        return ret;
    }

    public static String getLogoutLink(String oAuth, String username) {
        return "<a href=\"/user/logout.jsp\">" + username + " (" + oAuth + ")</a>";
    }

    public static boolean isValid(long userId, Class<?> clz) {
        return (userId > 0 && UsersTable.isValidUser(userId, clz));
    }

}
