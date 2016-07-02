package feedreader.security;

import feedreader.utils.BooleanUtils;
import javax.servlet.http.HttpServletRequest;

public class Parameter
{
    public static String asString(HttpServletRequest request, String name, String stringDef)
    {
        String str = request.getParameter(name);
        return (str == null) ? stringDef : str;
    }

    public static int asInt(HttpServletRequest request, String name, int intDef)
    {
        return (int) asLong(request, name, intDef);
    }

    public static long asLong(HttpServletRequest request, String name, long intDef)
    {
        String str = request.getParameter(name);
        return (str == null || str.isEmpty()) ? intDef : Long.valueOf(str);
    }
    
    public static boolean asBoolean(HttpServletRequest request, String name, boolean defaultValue)
    {
        String str = request.getParameter(name);
        if (str == null) return defaultValue;

        return BooleanUtils.isBoolean(str);
    }
}
