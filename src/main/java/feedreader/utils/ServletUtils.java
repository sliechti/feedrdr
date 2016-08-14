package feedreader.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.config.FeedAppConfig;
import feedreader.security.Parameter;
import feedreader.security.PasswordResetServlet;

public class ServletUtils {

    private static final String DEFAULT_LANG = "en";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    private static final String TMPL_BASE_URL = "baseUrl";
    private static final String TMPL_BASE_URL_LINK = "baseUrlLink";
    private static final String TMPL_HOME = "home";

    public static void pleaseTryAgainLater(HttpServletRequest request) {
        request.setAttribute("err", "There was a problem processing the request, please try again later");
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String location)
            throws ServletException, IOException {
        String contextPath = FeedAppConfig.BASE_APP_URL;

        String lang = Parameter.asString(req, "lang", DEFAULT_LANG);
        if (!FeedAppConfig.SUPPORTED_LANGS.contains(lang)) {
            lang = DEFAULT_LANG;
        }
        req.setAttribute("fwdURI", req.getRequestURI());
        req.setAttribute("language", lang);
        req.setAttribute("isLocal", ApplicationConfig.instance().isLocal());
        req.setAttribute(TMPL_HOME, PageUtils.getHome());
        req.setAttribute(TMPL_BASE_URL, contextPath);
        req.setAttribute(TMPL_BASE_URL_LINK, (contextPath.isEmpty()) ? "/" : contextPath + "/");
        req.getServletContext().getRequestDispatcher(location).forward(req, resp);
    }

    public static void sendJson(HttpServletResponse resp, String err) throws IOException {
        resp.setContentType(CONTENT_TYPE_JSON);
        resp.getWriter().write(err);
    }

    public static void sendJsonError(HttpServletResponse resp, int code, String error) throws IOException {
        sendJson(resp, JSONUtils.error(code, error));
    }

    public static void sendJsonError(HttpServletResponse resp, String error) throws IOException {
        sendJsonError(resp, 1, error);
    }

    public static void sendJsonSuccess(HttpServletResponse resp, String success) throws IOException {
        sendJson(resp, JSONUtils.success(success));
    }

    public static void setError(HttpServletRequest request, String error) {
        request.setAttribute(PasswordResetServlet.ATTR_ERROR, error);
    }

    public static void setInfo(HttpServletRequest req, String info) {
        req.setAttribute(PasswordResetServlet.ATTR_INFO, info);
    }

}
