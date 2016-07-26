package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.config.FeedAppConfig;
import feedreader.config.OAuthConfig;
import feedreader.utils.ApplicationConfig;
import feedreader.utils.PageUtils;

public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId != 0) {
            PageUtils.gotoHome(resp);
            return;
        }
        req.setAttribute("baseUrl", req.getContextPath());
        req.setAttribute("minifiedStr", "");
        req.setAttribute("oauthFacebooKey", OAuthConfig.FB_KEY);
        req.setAttribute("oauthGoogleKey", OAuthConfig.GOOGLE_KEY);
        req.setAttribute("oauthWindowsKey", OAuthConfig.LIVE_KEY);
        req.setAttribute("appName", FeedAppConfig.APP_NAME);
        if (ApplicationConfig.instance().isLocal()) {
            req.setAttribute("oauthDebug", "&debug=true");
        }
        getServletContext().getRequestDispatcher("/welcome.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
