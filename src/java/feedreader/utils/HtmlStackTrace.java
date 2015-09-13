package feedreader.utils;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class HtmlStackTrace {

    public static String get(StackTraceElement[] st, int max, String eol) {
        StringBuilder sb = new StringBuilder();

        int to = ((st.length > max) ? max : st.length);
        for (int x = 0; x < to; x++) {
            if (x == 0) {
                sb.append("<b>").append(st[x].toString()).append("</b>").append(eol);
            } else {
                sb.append(st[x].toString()).append(eol);
            }
        }

        return sb.toString();
    }

    public static void printRed(JspWriter out, StackTraceElement[] st, int max, String eol) throws IOException {
        out.write("<font color=\"red\">" + HtmlStackTrace.get(st, 10, "<br>") + "</font>");
    }

}
