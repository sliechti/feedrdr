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
                case -2:
                    setPageError(request, "Your account has been disabled.");
                    break;
            }
        }

        sendtoLoginPage(request, response);

    }

    private void setPageError(HttpServletRequest request, String error) {
        request.setAttribute("error", error);
    }

}
