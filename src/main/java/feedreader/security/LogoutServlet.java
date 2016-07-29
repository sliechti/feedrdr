package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.config.Constants;
import feedreader.utils.PageUtils;

@WebServlet(name = "logout", urlPatterns = { "/logout" })
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session.invalidate(req);
        CookieUtils.wipe(resp, Constants.USER_COOKIE);
        PageUtils.gotoStart(req, resp);
    }
}
