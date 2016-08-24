package feedreader.pages;

import javax.servlet.http.HttpServletRequest;

public class PageHeader {

    public static void hideEllipsis(HttpServletRequest req) {
        req.setAttribute("hideEllipsis", true);
    }

    public static void hideLeftMenu(HttpServletRequest req) {
        req.setAttribute("hideLeftMenu", true);
    }

    public static void showBackButton(HttpServletRequest req) {
        req.setAttribute("showBackButton", true);
    }

    public static void showSettingsMenuEntry(HttpServletRequest req) {
        req.setAttribute("showSettingsInMenu", true);
    }

}
