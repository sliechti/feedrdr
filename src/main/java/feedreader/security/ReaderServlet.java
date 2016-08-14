package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.pages.PageHeader;
import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;

@WebServlet(name = "reader", urlPatterns = { "/reader" })
public class ReaderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        PageHeader.showSettingsMenuEntry(req);
        ServletUtils.redirect(req, resp, "/pages/reader.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
