package feedreader.utils;

public class StringUtils 
{
    public static String cut(String str, int len)
    {
        if (len > str.length()) return str;
        return "<span title='"+ str +"'>" + str.substring(0, len) + "...</span>";
    }
}
