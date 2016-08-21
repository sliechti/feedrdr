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
            handleNotificationsPost(req, user);
        }
        gotoSettings(req, resp);
    }

    private void gotoSettings(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.redirect(req, resp, "/pages/settings.jsp");
    }

    private void handleNotificationsPost(HttpServletRequest req, UserData data) {
        data.setSubscribedForNewsletter(Parameter.asBoolean(req, "newsletter", false));
        data.setSubscribedToProductUpdates(Parameter.asBoolean(req, "updates", false));
        updateUsers(req, data);
    }

    private void updateUsers(HttpServletRequest request, UserData data) {
        logger.debug("updating user: {}", data);
        if (UsersTable.update(data) > 0) {
            request.setAttribute("infoMsg",
                    "Settings updated<hr>"
                    + "<a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>");
        } else {
            request.setAttribute("errMsg", "There was a problem updating your user settings. "
                    + "Please keep using your old settings until we figure out what went wrong.<br>");
        }
    }
}
