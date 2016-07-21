package feedreader.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class PwdUtils {

    public static String generate() {
        return RandomStringUtils.random(12, true, true);
    }

}
