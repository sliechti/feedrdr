package feedreader.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String location)
            throws ServletException, IOException {
        String contextPath = req.getContextPath();
        req.setAttribute("baseUrl", contextPath);
        req.setAttribute("baseUrlLink", (contextPath.isEmpty()) ? "/" : contextPath + "/");
        req.getServletContext().getRequestDispatcher(location).forward(req, resp);
    }

}
