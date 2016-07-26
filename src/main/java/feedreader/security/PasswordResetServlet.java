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
import feedreader.utils.ServletUtils;
import feedreader.utils.Validate;

@WebServlet(name = "password_reset", urlPatterns = { "/password_reset" })
public class PasswordResetServlet extends HttpServlet {

    public static final String ATTR_ERROR = "err";
    public static final String ATTR_INFO = "info";
    private static final String ATTR_PWD_CHANGED = "pwdChanged";
    private static final String ATTR_VALID_CODE = "validCode";
    private static final String LANG_EMAIL_WITH_RESET_INSTRUCTIONS_SENT = "E-mail with instructions sent";
    private static final String LANG_PASSWORDS_CHANGED = "Password changed";
    private static final String LANG_PASSWORDS_DON_T_MATCH = "Passwords don't match";
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetServlet.class);
    private static final String PARAM_CODE = "code";
    private static final String PARAM_PWD1 = "pwd1";
    private static final String PARAM_PWD2 = "pwd2";
    private static final String PARAM_RECOVER_EMAIL = "email";
    private static final String PASSWORD_RESET_JSP = "/password_reset.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String code = Parameter.asString(request, PARAM_CODE, "");
        if (!code.isEmpty()) {
            UserData data = UsersTable.getFromForgotCode(code);
            request.setAttribute(ATTR_VALID_CODE, !data.isNull());
        }
        redirectToDefaultPage(request, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        if (!Parameter.asString(request, PARAM_RECOVER_EMAIL, "").isEmpty()) {
            String recoverEmail = Parameter.asString(request, PARAM_RECOVER_EMAIL, "");
            logger.info("recover_email: {}", recoverEmail);
            if (Validate.isValidEmailAddress(recoverEmail)) {
                UserData data = UsersTable.get(Parameter.asString(request, PARAM_RECOVER_EMAIL, ""));
                if (data.getUserId() > 0) {
                    int r = UsersTable.setForgotPassword(data.getUserId());
                    if (r >= 0) {
                        ServletUtils.setInfo(request, LANG_EMAIL_WITH_RESET_INSTRUCTIONS_SENT);
                    } else {
                        ServletUtils.pleaseTryAgainLater(request);
                    }
                }
            }
        } else if (Parameter.isSet(request, PARAM_CODE)) {
            String code = Parameter.asString(request, PARAM_CODE, "");
            UserData data = UsersTable.getFromForgotCode(code);

            if (!data.isNull() && Parameter.isSet(request, "submit")) {
                String pwd1 = Parameter.asString(request, PARAM_PWD1, "");
                String pwd2 = Parameter.asString(request, PARAM_PWD2, "");
                if (!pwd1.isEmpty() && !pwd2.isEmpty() && pwd1.equals(pwd2)) {
                    if (Validate.isValidPassword(pwd1)) {
                        int r = UsersTable.setNewPassword(data, pwd1);
                        if (r >= 0) {
                            ServletUtils.setInfo(request, LANG_PASSWORDS_CHANGED);
                            request.setAttribute(ATTR_PWD_CHANGED, true);
                        } else {
                            ServletUtils.pleaseTryAgainLater(request);
                        }
                    }
                } else {
                    ServletUtils.setError(request, LANG_PASSWORDS_DON_T_MATCH);
                }

            }
        }
        redirectToDefaultPage(request, resp);
    }

    private void redirectToDefaultPage(HttpServletRequest request, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletUtils.redirect(request, resp, PASSWORD_RESET_JSP);
    }
}
