package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.entities.UserData;
import feedreader.store.UsersTable;
import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;
import feedreader.utils.Validate;

@WebServlet(name = "settings", urlPatterns = {
        "/settings",
        "/settings/profiles",
        "/settings/notifications",
        "/settings/account" })
public class SettingsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SettingsServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        gotoSettings(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        UserData user = UsersTable.get(userId);
        String uri = req.getRequestURI();
        if (uri.endsWith("/notifications")) {
            handleNotificationsUpdate(req, user);
        } else if (uri.endsWith("/account") ||
                uri.endsWith("/settings")) {
            handleAccountsUpdate(req, user);
        }
        gotoSettings(req, resp);
    }

    private void gotoSettings(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.redirect(req, resp, "/pages/settings.jsp");
    }

    private void handleAccountsUpdate(HttpServletRequest req, UserData data) {
        if (Parameter.isSet(req, "email")) {
            handleEmailChange(req, data);
        } else if (Parameter.isSet(req, "pwd")) {
            handlePasswordChange(req, data);
        }
    }

    private void handlePasswordChange(HttpServletRequest req, UserData data) {
        String currentPwd = Parameter.asString(req, "current", "");
        if (!currentPwd.equals(data.getPwd())) {
            ServletUtils.setError(req, "Wrong password");
            return;
        }

        String pwd = Parameter.asString(req, "pwd", "1");
        String confirm = Parameter.asString(req, "confirm", "2");
        if (!pwd.isEmpty() && confirm.equals(pwd)) {
            if (!Validate.isValidPassword(confirm)) {
                ServletUtils.setError(req, Validate.getPasswordRules());
            } else {
                data.setPwd(confirm);
                if (UsersTable.update(data) > 0) {
                    ServletUtils.setInfo(req, "Password changed");
                } else {
                    ServletUtils.setError(req, "Failed to save new settings, please try again later");
                }
            }
        }
    }

    private void handleEmailChange(HttpServletRequest req, UserData data) {
        String changeTo = Parameter.asString(req, "email", "");
        if (changeTo.equalsIgnoreCase(data.getEmail())) {
            ServletUtils.setError(req, "Email didn't change");
        } else if (Validate.isValidEmailAddress(changeTo)) {
            UserData checkAlreadyUsed = UsersTable.get(changeTo);
            if (checkAlreadyUsed.getUserId() == 0) {
                data.setEmail(changeTo);
                if (UsersTable.update(data) == 1) {
                    UsersTable.unverify(data);
                    ServletUtils.setInfo(req, "Email updated, "
                            + "please check your inbox for instructions on how to verify your new email.");
                } else {
                    ServletUtils.setError(req,
                            "There was a problem updating your settings. Please try again later.");
                }
            } else {
                ServletUtils.setError(req, "This Email is already being used. Please choose another.");
            }
        } else {
            ServletUtils.setError(req, String.format("The email %s appears to be invalid", changeTo));
        }
    }

    private void handleNotificationsUpdate(HttpServletRequest req, UserData data) {
        data.setSubscribedForNewsletter(Parameter.asBoolean(req, "newsletter", false));
        data.setSubscribedToProductUpdates(Parameter.asBoolean(req, "updates", false));
        updateUsers(req, data);
    }

    private void updateUsers(HttpServletRequest request, UserData data) {
        logger.debug("updating user: {}", data);
        if (UsersTable.update(data) > 0) {
            ServletUtils.setInfo(request, "Settings updated<hr>"
                    + "<a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>");
        } else {
            ServletUtils.setError(request, "There was a problem updating your user settings. "
                    + "Please keep using your old settings until we figure out what went wrong.<br>");
        }
    }
}
