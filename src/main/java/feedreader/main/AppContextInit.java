package feedreader.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import feedreader.config.Constants;
import feedreader.config.DeveloperConfig;
import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.config.OAuthConfig;
import feedreader.cron.CronFetchNews;
import feedreader.cron.CronForgotPasswordEmail;
import feedreader.cron.CronNewUsersEmail;
import feedreader.cron.CronTimeUtils;
import feedreader.log.Logger;
import feedreader.store.Database;
import feedreader.time.CurrentTime;
import feedreader.utils.SimpleMail;

/**
 * Called when application context is initialized.
 *
 * @see web.xml
 */
public class AppContextInit implements ServletContextListener {

	public static Class<?> clz = AppContextInit.class;
	private static final String BASE_ADMIN_URL_PROP_KEY = "base_admin_url";
	private static final String BASE_URL_EMAIL_PROP_KEY = "base_url_email";
	private static final String BASE_URL_PROP_KEY = "base_url";
	private static ServletContextEvent context;
	private static final String DOWNLOAD_XML_PATH_PRO_KEY = "download_xml_path";
	private static String downloadXmlPath = "";
	private static final String INTERVAL_FETCH_PROP_KEY = "interval_fetch_seconds";
	private static final String LOG_FILE_PROP_KEY = "log_file";
	private static final String LOG_LEVEL_PROP_KEY = "log_level";
	private static String logFile = "";

	private ScheduledExecutorService sc;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Logger.info(clz).log("**** contextDestroyed **** ").end();

		if (sc != null) {
			sc.shutdown();
			try {
				sc.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				Logger.error(clz).log(ex.getMessage()).end();
			}
		}
		Database.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		context = sce;
		System.err.println(" ***** " + clz.getSimpleName() + " initialiazing. ***** ");

		if (isDevPc()) {
			FeedAppConfig.APP_ENV = Constants.ENV_DEV_NAME;
		} else {
			FeedAppConfig.APP_ENV = Constants.ENV_PROD_NAME;
		}

		Logger.error(clz).log("Initializing app, running auto configuration.").end();

		setupLogger();
		setupSmtpOptions();
		setupOAuth();

		downloadXmlPath = loadPropertiesFile("conf/conf").getProperty(DOWNLOAD_XML_PATH_PRO_KEY);
		int delayFetch = Integer.parseInt(loadPropertiesFile("conf/conf").getProperty(INTERVAL_FETCH_PROP_KEY));
		String baseUrl = loadPropertiesFile("conf/conf").getProperty(BASE_URL_PROP_KEY);
		String baseUrlEmail = loadPropertiesFile("conf/conf").getProperty(BASE_URL_EMAIL_PROP_KEY);
		baseUrlEmail += baseUrl;
		String baseAdminUrl = loadPropertiesFile("conf/conf").getProperty(BASE_ADMIN_URL_PROP_KEY);

		Logger.info(clz).log("Param Download XML Path   : [").log(downloadXmlPath).log("]").end();
		Logger.info(clz).log("Param Delay Fetch in Secs : [").log(delayFetch).log("]").end();
		Logger.info(clz).log("Param BaseURL             : [").log(baseUrl).log("]").end();
		Logger.info(clz).log("Param BaseURL for Emails  : [").log(baseUrlEmail).log("]").end();
		Logger.info(clz).log("Param BaseAdminURL        : [").log(baseAdminUrl).log("]").end();

		if (Environment.isProd()) {
			try {
				Logger.get().setErrorLevel();
				Logger.error(clz).log("log set to error.").end();
			} catch (IOException ex) {
				// noop
			}
		} else {
			FeedAppConfig.APP_NAME = Environment.name() + "-" + FeedAppConfig.APP_NAME;
			FeedAppConfig.XML_SAVE = true;
		}

		FeedAppConfig.DOWNLOAD_XML_PATH = downloadXmlPath;
		FeedAppConfig.DELAY_FETCH_IN_S = delayFetch;
		FeedAppConfig.BASE_APP_URL = baseUrl;
		FeedAppConfig.BASE_APP_URL_EMAIL = baseUrlEmail;
		FeedAppConfig.BASE_ADMIN_URL = baseAdminUrl;

		if (setupDatabase()) {
			if (Environment.isDev()) {
				Logger.info(clz).log("Checking for developer configration options.").end();
				// FeedAppConfig.FETCH_RUN_START_FETCHING = true;
				// FeedAppConfig.FETCH_RUN_START_VALIDATION = true;
				Logger.info(clz).log("FETCH_RUN_START_FETCHING true FETCH_RUN_START_VALIDATION true").end();
				DeveloperConfig.run();
			}

			startCronThreads(sce);
		}

	}

	private void setupOAuth() {
    	Properties props = loadPropertiesFile("conf/oauth");
    	Logger.info(clz).log("setting up oauth, keys found: ").log(props.size()).end();
    	OAuthConfig.FB_DEV_KEY = props.getProperty("fb.dev", "see.oauth.properties");
    	OAuthConfig.FB_PROD_KEY = props.getProperty("fb.prod", "see.oauth.properties");
    	OAuthConfig.GOOGLE_KEY = props.getProperty("google", "see.oauth.properties");
    	OAuthConfig.LIVE_KEY = props.getProperty("ms.live", "see.oauth.properties");
	}

	private boolean isDevPc() {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			hostName = "unknown " + ex.getMessage();
		}

		Logger.info(clz).log("Running on host ").log(hostName).log(", environment set to ").log(FeedAppConfig.APP_ENV)
				.end();

		return hostName.equalsIgnoreCase(FeedAppConfig.DEV_PC);
	}

	private Properties loadPropertiesFile(String file) {
		String path = file + (Environment.isDev() ? ".dev" : ".prod") + ".properties";
		String propertiesFile = "/WEB-INF/" + path;
		InputStream propertiesStream = getContext().getServletContext().getResourceAsStream(propertiesFile);
		Properties properties = new Properties();
		try {
			properties.load(propertiesStream);
		} catch (Exception e) {
			Logger.error(clz).log("error loading properties file " + propertiesFile);
		}
		return properties;
	}

	private boolean setupDatabase() {
		Logger.info(clz).log("setting up database").end();
		Database.start(loadPropertiesFile("database/database"));
		Logger.info(clz).log("database ok").end();
		return true;
	}

	private void setupLogger() {
		Properties configProperties = loadPropertiesFile("conf/conf");
		int logLevel = Integer.parseInt(configProperties.getProperty(LOG_LEVEL_PROP_KEY));
		String logFileProp = configProperties.getProperty(LOG_FILE_PROP_KEY);
		try {
			Logger.get().setLevel(Logger.LogLevels.fromVal(logLevel));
			if (!Environment.isDev()) {
				Logger.get().setWriter(new FileWriter(logFileProp, true));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupSmtpOptions() {
		Properties mailProperties = loadPropertiesFile("conf/mail");

		SimpleMail.configure(mailProperties);
	}

	private void startCronThreads(ServletContextEvent sce) {
		Logger.info(clz).log("starting threads").end();

		sc = Executors.newSingleThreadScheduledExecutor();

		long t = CurrentTime.inGMT();
		sc.scheduleAtFixedRate(new CronForgotPasswordEmail(t, sce.getServletContext()), 0,
				FeedAppConfig.DELAY_CHECK_FORGOT_PASSWORD, TimeUnit.SECONDS);
		sc.scheduleAtFixedRate(new CronTimeUtils(), 0, 1, TimeUnit.MINUTES);
		sc.scheduleAtFixedRate(new CronNewUsersEmail(t, sce.getServletContext()), 0,
				FeedAppConfig.DELAY_CHECK_NEW_USERS_EMAIL, TimeUnit.SECONDS);

		if (FeedAppConfig.FETCH_RUN_START_FETCHING) {
			sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(false), 0, FeedAppConfig.DELAY_FETCH_IN_S,
					TimeUnit.SECONDS);
		} else {
			Logger.info(clz).log("*** Not starting fetch thread as defined in FeedAppConfig.FETCH_RUN_START_FETCHING.")
					.end();
		}

		if (FeedAppConfig.FETCH_RUN_START_VALIDATION) {
			sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(true), 0, FeedAppConfig.DELAY_FETCH_IN_S,
					TimeUnit.SECONDS);
		} else {
			Logger.info(clz)
					.log("*** Not starting fetch thread as defined in FeedAppConfig.FETCH_RUN_START_VALIDATION.").end();
		}
	}

	public static ServletContextEvent getContext() {
		return context;
	}

	public static String getDownloadXmlPath() {
		return downloadXmlPath;
	}

	public static String getLogFile() {
		return logFile;
	}

}
