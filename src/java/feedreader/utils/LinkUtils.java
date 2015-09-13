package feedreader.utils;

public class LinkUtils {

    public static String getLink(String href, String name) {
        return "<a href='" + href + "'>" + name + "</a>";
    }

    public static String getExternalLink(String href, String name) {
        return "<a href='" + href + "' target='_blank'>" + name + "</a>";
    }
}
