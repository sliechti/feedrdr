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
		req.setAttribute("baseUrl", FeedAppConfig.BASE_APP_URL);
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

//	String info = "";
//	String err = "";
//
//	if (request.getMethod() == "POST") {
//		if (!Parameter.asString(request, "login", "").isEmpty()) {
//			int r = UserSession.authenticate(request, response);
//			switch (r) {
//				case 1 :
//					PageUtils.gotoHome(response);
//					return;
//
//				case 0 :
//					err = "Couldn't initialize the user session. If you are still experiencing this issue "
//							+ "after a couple of retries, please let us know.";
//					break;
//
//				case -1 :
//					err = "Wrong username/email or password combination.";
//					break;
//			}
 //		}
//
//		if (Parameter.asString(request, "signup", "").equalsIgnoreCase("signup")) {
//			UserSession.createNew(response, request);
//			return;
//		} ;
//
//		if (!Parameter.asString(request, "recover_email", "").isEmpty()) {
//			String recoverEmail = Parameter.asString(request, "recover_email", "");
//			if (Validate.isValidEmailAddress(recoverEmail)) {
//				UserData data = UsersTable.get(Parameter.asString(request, "recover_email", ""));
//				if (data.getUserId() > 0) {
//					byte[] code = SimpleEncryption.encrypt(FeedAppConfig.ENC_KEY_REOVER_EMAIL_CODE, true,
//							data.getEmail());
//					int r = UsersTable.setForgotPassword(data.getUserId(), new String(code));
//					if (r >= 0) {
//						info = "An email will be sent to "
//								+ Parameter.asString(request, "recover_email", "user@domain")
//								+ ", please contact us if you don't receive it in the next couple of minutes. Thank you.";
//					} else {
//						err = "There was a problem. We were informed and will have a look as soon as possible.";
//					}
//				} ;
//			} else {
//				err = "Invalid email. ";
//			}
//		}
//	}
//
//	long userId = UserSession.getUserId(request);
//	if (userId > 0) {
//		PageUtils.gotoHome(response);
//		return;
//	}
//
//	String minjs = (Environment.isProd() ? ".min.js" : ".js");
//	String mincss = (Environment.isProd() ? ".min.css" : ".css");
//%>
}
