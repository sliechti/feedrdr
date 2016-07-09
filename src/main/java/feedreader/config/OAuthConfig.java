package feedreader.config;

public class OAuthConfig {

    public static String FB_DEV_KEY = "";
    public static String FB_PROD_KEY = "";
    public static String GOOGLE_KEY = "";
    public static String LIVE_KEY = "";

    public static String getFbKey() {
        if (Environment.isProd()) {
            return FB_PROD_KEY;
        }
        return FB_DEV_KEY;
    }

}
