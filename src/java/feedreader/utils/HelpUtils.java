package feedreader.utils;

public class HelpUtils {
    
    public static String MouseOver(String topic) {
        return "<a href=\"#\" onMouseOver=\"console.log('help: " + topic + "')\">help</a>";
    }

    public static String MouseOver(String hrefText, String topic) {
        return "<a href=\"#\" onMouseOver=\"console.log('help: " + topic + "')\">" + hrefText + "</a>";
    }

}