package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;

@WebServlet(name = "login", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendtoLoginPage(req, resp);
    }

    private void sendtoLoginPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletUtils.redirect(req, resp, "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long userId = UserSession.getUserId(request);
        if (userId > 0) {
            PageUtils.gotoHome(response);
            return;
        }

        if (!Parameter.asString(request, "login", "").isEmpty()) {
            int r = UserSession.authenticate(request, response);
            switch (r) {
                case 1:
                    PageUtils.gotoHome(response);
                    return;
                case 0:
                    setPageError(request,
                            "Couldn't initialize the user session. If you are still experiencing this issue"
                                    + " after a couple of retries, please let us know.");
                    break;
                case -1:
                    setPageError(request, "Wrong username/email or password combination.");
                    break;
            }
        }

        sendtoLoginPage(request, response);

        //
        // if (!Parameter.asString(request, "recover_email", "").isEmpty()) {
        // String recoverEmail = Parameter.asString(request, "recover_email", "");
        // if (Validate.isValidEmailAddress(recoverEmail)) {
        // UserData data = UsersTable.get(Parameter.asString(request, "recover_email", ""));
        // if (data.getUserId() > 0) {
        // byte[] code = SimpleEncryption.encrypt(FeedAppConfig.ENC_KEY_REOVER_EMAIL_CODE, true, data.getEmail());
        // int r = UsersTable.setForgotPassword(data.getUserId(), new String(code));
        // if (r >= 0) {
        // info = "An email will be sent to "
        // + Parameter.asString(request, "recover_email", "user@domain")
        // + ", please contact us if you don't receive it in the next couple of minutes. Thank you.";
        // } else {
        // err = "There was a problem. We were informed and will have a look as soon as possible.";
        // }
        // }
        // ;
        // } else {
        // err = "Invalid email. ";
        // }
        // }
    }

    private void setPageError(HttpServletRequest request, String error) {
        request.setAttribute("error", error);
    }

}
