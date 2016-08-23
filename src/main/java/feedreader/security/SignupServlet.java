package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.entities.UserData;
import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;
import feedreader.utils.Validate;

@WebServlet(name = "signup", urlPatterns = "/signup")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PageUtils.gotoStart(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || !Validate.isValidEmailAddress(email)) {
            req.setAttribute("invalidEmail", true);
            req.setAttribute("email", email);
            ServletUtils.redirect(req, resp, "/welcome.jsp");
            return;
        }

        UserData user = UserSession.createNew(email, req.getLocale());
        if (user.getUserId() == 0) {
            req.setAttribute("emailKnown", true);
            ServletUtils.redirect(req, resp, "/welcome.jsp");
            return;
        }

        UserSession.initUserSession(req, user);
        PageUtils.redirect(resp, "/wizard");
    }
}
