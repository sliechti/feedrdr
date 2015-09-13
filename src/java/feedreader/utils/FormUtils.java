package feedreader.utils;

import feedreader.security.Parameter;
import javax.servlet.http.HttpServletRequest;

public class FormUtils {

    public static String checked(HttpServletRequest req, String name) {
        if (Parameter.asBoolean(req, name, false)) {
            return " checked"; // input type=checkbox
        }

        return "";
    }
}
