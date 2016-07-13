package feedreader.config;

public class DeveloperConfig {
    public static void run() {
        FeedAppConfig.FETCH_RUN_START_FETCHING = false;
        FeedAppConfig.FETCH_RUN_START_VALIDATION = false;
    }
}
