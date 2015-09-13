package feedreader.utils;

import java.io.IOException;
import java.io.InputStream;

public class TextUtils
{
    public static boolean isSpace(char c)
    {
        return (c == ' ' || c == '\r' || c == '\n');
    }

    public static StringBuilder toStringBuilder(InputStream is, StringBuilder sb, boolean resetLength) throws IOException
    {
        if (resetLength) {
            sb.setLength(0);
        }

        sb.setLength(is.available());

        int c = -1;
        while ((c = is.read()) != -1) {
            sb.append((char)c);
        }

        return sb;
    }

}
