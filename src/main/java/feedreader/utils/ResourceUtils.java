package feedreader.utils;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {

    private static final String UTF_8 = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * Loads a resource located in the classpath as a String. Expects the file to be UTF-8 encoded.
     *
     * This method doesn't throw since the expectation is that this method is to be used only for templates or
     * other static files, located at very specific locations and known well before-hand.
     *
     * Any errors in those files should be catched by the developer or if they reach production the error is sent by
     * email.
     *
     * @param classpath
     * @return the String representing the file or empty on error
     */
    public static String loadResource(String classpath) {
        String ret = "";
        try {
            InputStream is = ClassUtils.loadResource(ResourceUtils.class, classpath);
            ret = IOUtils.toString(is, UTF_8);
        } catch (Exception e) {
            logger.error("failed to load resource: {}, error: {}", e, classpath, e.getMessage());
        }
        return ret;
    }
}
