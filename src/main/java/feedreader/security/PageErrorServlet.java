package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.utils.ApplicationConfig;
import feedreader.utils.HtmlStackTrace;
import feedreader.utils.ServletUtils;
import feedreader.utils.SimpleMail;

@WebServlet(name = "500", urlPatterns = { "/e/500" })
public class PageErrorServlet extends HttpServlet {

    private static final SimpleMail mail = new SimpleMail();
    private static final Logger logger = LoggerFactory.getLogger(PageErrorServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Analyze the servlet exception
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) req.getAttribute("javax.servlet.error.servlet_name");
        String errorMessage = (throwable != null && throwable.getMessage() != null) ? throwable.getMessage() : "no-message";
        logger.error("servlet: {}, code: {}, error: {}", throwable, servletName, statusCode, errorMessage);

        boolean isLocal = ApplicationConfig.instance().isLocal();
        String local = (isLocal) ? "Local" : "";
        req.setAttribute("isLocal", isLocal);
        if (!isLocal) {
            try {
                mail.send("stacktrace@feedrdr.co", "StackTrace",
                        "steven@feedrdr.co", "Steven Liechti",
                        "StackTrace " + local, errorMessage + "" +
                                HtmlStackTrace.get(throwable, Integer.MAX_VALUE, "\n"));
            } catch (Exception e) {
                logger.error("failed to send stacktrace: {}", e, e.getMessage());
            }
        }
        ServletUtils.redirect(req, resp, "/error/500.jsp");
    }

}
