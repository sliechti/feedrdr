package feedreader.config;

public class OAuthConfig {

    public static final String FB_DEV_KEY = "REPLACE_FB_DEV_KEY";
    public static final String FB_PROD_KEY = "REPLACE_FB_PROD_KEY";
    public static final String GOOGLE_KEY = "REPLACE_GOOGLE_KEY";
    public static final String LIVE_KEY = "REPLACE_MS_LIVE_KEY";

    public static String getFbKey() {
        if (Environment.isProd()) {
            return FB_PROD_KEY;
        }
        return FB_DEV_KEY;
    }
}
