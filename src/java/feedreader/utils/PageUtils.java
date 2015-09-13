package feedreader.utils;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class PageUtils {
    
    public static String getHome() {
        return FeedAppConfig.BASE_APP_URL + Constants.SESSION_START_PAGE;
    }

    public static void gotoHome(HttpServletResponse response) throws IOException {
        response.sendRedirect(getHome());
    }

    public static void gotoStart(HttpServletResponse response) throws IOException {
        response.sendRedirect(FeedAppConfig.BASE_APP_URL);
    }

    public static String getPath(String href) {
        return FeedAppConfig.BASE_APP_URL + href;
    }

    public static String getAdminBase() {
        return FeedAppConfig.BASE_ADMIN_URL;
    }

    public static void gotoAdminHome(HttpServletResponse response) throws IOException {
        response.sendRedirect(getAdminBase() + "/" + Constants.SESSION_ADMIN_START_PAGE);
    }

    public static void gotoAdminLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(getAdminBase() + "/" + Constants.SESSION_ADMIN_LOGIN_PAGE);
    }

    public static void redirect(HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(FeedAppConfig.BASE_APP_URL + location);
    }
}
