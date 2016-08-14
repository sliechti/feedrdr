package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.utils.ServletUtils;

@WebServlet(name = "404", urlPatterns = { "/e/404" })
public class PageNotFoundServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PageNotFoundServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("page not found: {}", req.getAttribute("javax.servlet.error.request_uri"));
        ServletUtils.redirect(req, resp, "/404.jsp");
    }
}
