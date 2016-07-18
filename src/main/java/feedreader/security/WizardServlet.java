package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.utils.ServletUtils;

@WebServlet(name="newUserWizard", urlPatterns={"/pages/wizard"})
public class WizardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletUtils.redirect(req, resp, "/pages/wizard.jsp");
    }
}
