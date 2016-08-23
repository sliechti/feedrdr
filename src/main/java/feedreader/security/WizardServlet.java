package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.entities.UserData;
import feedreader.security.WizardServlet.PageHeader;
import feedreader.store.UsersTable;
import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;
import feedreader.utils.Validate;

@WebServlet(name = "newUserWizard", urlPatterns = { "/wizard" })
public class WizardServlet extends HttpServlet {

    private static final String JSON_PARAM_PWD1 = "pwd1";

    private static final String JSON_PARAM_PWD2 = "pwd2";
    private static final String JSON_PARAM_WIZARD_STEP = "wizard-step";
    private static final String PAGES_WIZARD_JSP = "/pages/wizard.jsp";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        UserData user = UsersTable.get(userId);

        req.setAttribute("user", user);
        PageHeader.hideEllipsis(req);
        PageHeader.hideLeftMenu(req);
        ServletUtils.redirect(req, resp, PAGES_WIZARD_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Parameter.isSet(req, JSON_PARAM_WIZARD_STEP)) {
            UserData user = UserSession.getCurrentUser(req);
            if (user.isNull()) {
                PageUtils.gotoStart(req, resp);
                return;
            }

            if (!user.isGenerated()) {
                ServletUtils.sendJsonError(resp, 100, "Password already set");
                return;
            }

            String pwd1 = Parameter.asString(req, JSON_PARAM_PWD1, "");
            String pwd2 = Parameter.asString(req, JSON_PARAM_PWD2, "");
            if (!pwd1.isEmpty() && !pwd1.equals(pwd2)) {
                ServletUtils.sendJsonError(resp, "Passwords don't match");
                return;
            }

            if (Validate.isValidPassword(pwd1)) {
                user.setPwd(pwd1);
                if (UsersTable.update(user) > 0) {
                    ServletUtils.sendJsonSuccess(resp, "password set");
                } else {
                    ServletUtils.sendJsonError(resp, "There was a problem updating your password. "
                            + "We wre informed and will check as soon as possible.");
                }
            } else {
                ServletUtils.sendJsonError(resp, Validate.getPasswordRules());
            }
            return;
        }

        ServletUtils.redirect(req, resp, PAGES_WIZARD_JSP);
    }

    public static class PageHeader {

        private static void hideEllipsis(HttpServletRequest req) {
            req.setAttribute("hideEllipsis", true);
        }

        private static void hideLeftMenu(HttpServletRequest req) {
            req.setAttribute("hideLeftMenu", true);
        }

    }
}
