package feedreader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import feedreader.entities.UserData;
import feedreader.store.UsersTable;
import feedreader.utils.ResourceUtils;
import feedreader.utils.ServletUtils;

@WebServlet(name = "verify", urlPatterns = { "/verify" })
public class VerifyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = Parameter.asString(req, "code", "");
        String msg = "Unknown code";
        if (!code.isEmpty()) {
            UserData data = UsersTable.getFromRegCode(code);
            if (data.getUserId() != 0) {
                UsersTable.verify(data);
                msg = ResourceUtils.loadResource("templates/accountverified.tmpl");
                req.setAttribute("redirectIn", 7);
                req.setAttribute("verified", true);
            }
        }
        req.setAttribute("code", code);
        req.setAttribute("msg", msg);

        ServletUtils.redirect(req, resp, "/verify.jsp");
    }

}
