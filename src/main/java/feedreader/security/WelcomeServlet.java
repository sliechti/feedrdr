package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.config.OAuthConfig;

public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("baseUrl", req.getContextPath());
        req.setAttribute("minifiedStr", "");
        req.setAttribute("oauthFacebooKey", OAuthConfig.getFbKey());
        req.setAttribute("oauthGoogleKey", OAuthConfig.GOOGLE_KEY);
        req.setAttribute("oauthWindowKey", OAuthConfig.LIVE_KEY);
        req.setAttribute("appName", FeedAppConfig.APP_NAME);
        if (Environment.isDev()) {
            req.setAttribute("oauthDebug", "&debug=true");
        }
        getServletContext().getRequestDispatcher("/welcome.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


}
