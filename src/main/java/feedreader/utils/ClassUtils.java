package feedreader.utils;

import java.io.InputStream;

public class ClassUtils {

    public static InputStream loadResource(Object clzz, String res) {
        return clzz.getClass().getClassLoader().getResourceAsStream(res);
    }

    public static InputStream loadResource(Class<?> clzz, String res) {
        return clzz.getClassLoader().getResourceAsStream(res);
    }

}
