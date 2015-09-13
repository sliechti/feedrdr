package feedreader.config;

/**
 * Application Environment Variables.
 */
public class Environment {
	
	public static boolean isDev() {
		return FeedAppConfig.APP_ENV.equals(Constants.ENV_DEV_NAME);
	}

	public static String name() {
		return FeedAppConfig.APP_ENV;
	}

	public static boolean isProd() {
		return FeedAppConfig.APP_ENV.equals(Constants.ENV_PROD_NAME);
	}

}
