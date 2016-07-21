package feedreader.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.security.PasswordResetServlet;

public class ServletUtils {

    public static void setInfo(HttpServletRequest req, String info) {
        req.setAttribute(PasswordResetServlet.ATTR_INFO, info);
    }

    public static void setError(HttpServletRequest request, String error) {
        request.setAttribute(PasswordResetServlet.ATTR_ERROR, error);
    }

    public static void pleaseTryAgainLater(HttpServletRequest request) {
        request.setAttribute("err", "There was a problem processing the request, please try again later");
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String location)
            throws ServletException, IOException {
        String contextPath = req.getContextPath();
        req.setAttribute("baseUrl", contextPath);
        req.setAttribute("baseUrlLink", (contextPath.isEmpty()) ? "/" : contextPath + "/");
        req.getServletContext().getRequestDispatcher(location).forward(req, resp);
    }

}
