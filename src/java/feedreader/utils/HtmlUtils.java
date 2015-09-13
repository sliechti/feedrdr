package feedreader.utils;

import javax.servlet.http.HttpServletRequest;

public class HtmlUtils {
    
    public static String getLocalLink(HttpServletRequest request, String linkName, String param, String val) {
        return "<a href='" + request.getRequestURI() + "?" + param + "=" + val + "'>" + linkName + "</a>";
    }

    public static String getOnClickLink(HttpServletRequest request, String onClick, String linkName) {
        return "<a href='" + request.getRequestURI() + "' onClick='" + onClick + "'>" + linkName + "</a>";
    }

}
