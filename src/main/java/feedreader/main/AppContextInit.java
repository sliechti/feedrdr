package feedreader.main;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.util.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.config.DeveloperConfig;
import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.config.OAuthConfig;
import feedreader.cron.CronFetchNews;
import feedreader.cron.CronForgotPasswordEmail;
import feedreader.cron.CronNewUsersEmail;
import feedreader.cron.CronTimeUtils;
import feedreader.store.Database;
import feedreader.utils.ApplicationConfig;

/**
 * Called when application context is initialized.
 *
 * @see web.xml
 */
public class AppContextInit implements ServletContextListener {

    private static final String BASE_ADMIN_URL_PROP_KEY = "base_admin_url";
    private static final String BASE_URL_EMAIL_PROP_KEY = "base_url_email";
    private static final String BASE_URL_PROP_KEY = "base_url";
    private static final String DOWNLOAD_XML_PATH_PRO_KEY = "download_xml_path";
    private static String downloadXmlPath = "";
    private static final String INTERVAL_FETCH_PROP_KEY = "interval_fetch_seconds";
    private static final Logger logger = LoggerFactory.getLogger(AppContextInit.class);
    private static final String LOG_FILE_PROP_KEY = "log_file";
    private static final String LOG_LEVEL_PROP_KEY = "log_level";
    private static String logFile = "";
    private static ServletContextEvent servletContext;

    private ApplicationConfig appConfig;
    private Set<String> prodServers = new HashSet<>(Arrays.asList("vultr.guest"));
    private ScheduledExecutorService sc;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("**** contextDestroyed **** ");

        if (sc != null) {
            sc.shutdown();
            try {
                sc.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                logger.error("context shutdown error {}", ex, ex.getMessage());
            }
        }
        Database.stop();
    }

    @Override
    public void contextInitialized(ServletContextEvent context) {
        servletContext = context;
        logger.info(" ***** initialiazing. ***** ");

        if (isDevPc()) {
            FeedAppConfig.APP_ENV = Constants.ENV_DEV_NAME;
        } else {
            FeedAppConfig.APP_ENV = Constants.ENV_PROD_NAME;
        }

        logger.info("setting environment to: {}", FeedAppConfig.APP_ENV);
        logger.info("server info: {}, built: {}, number: {}", ServerInfo.getServerInfo(),
                ServerInfo.getServerBuilt(), ServerInfo.getServerNumber());
        printClassPath();
        appConfig = ApplicationConfig.instance();

        setupLogger();
        setupOAuth();

        downloadXmlPath = appConfig.getString(DOWNLOAD_XML_PATH_PRO_KEY);
        int delayFetch = appConfig.getInt(INTERVAL_FETCH_PROP_KEY);
        String baseUrl = appConfig.getString(BASE_URL_PROP_KEY);
        String baseUrlEmail = appConfig.getString(BASE_URL_EMAIL_PROP_KEY);
        baseUrlEmail += baseUrl;
        String baseAdminUrl = appConfig.getString(BASE_ADMIN_URL_PROP_KEY);

        logger.info("Param Download XML Path   : [{}]", downloadXmlPath);
        logger.info("Param Delay Fetch in Secs : [{}]", delayFetch);
        logger.info("Param BaseURL             : [{}]", baseUrl);
        logger.info("Param BaseURL for Emails  : [{}]", baseUrlEmail);
        logger.info("Param BaseAdminURL        : [{}]", baseAdminUrl);

        if (Environment.isProd()) {
            try {
                feedreader.log.Logger.get().setErrorLevel();
            } catch (IOException ex) {
                logger.error("error setting old Logger error level: {}", ex, ex.getMessage());
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
                logger.info("Checking for developer configration options.");
                // FeedAppConfig.FETCH_RUN_START_FETCHING = true;
                // FeedAppConfig.FETCH_RUN_START_VALIDATION = true;
                logger.info("FETCH_RUN_START_FETCHING true FETCH_RUN_START_VALIDATION true");
                DeveloperConfig.run();
            } else {
                FeedAppConfig.FETCH_RUN_START_FETCHING = true;
                FeedAppConfig.FETCH_RUN_START_VALIDATION = true;
            }

            startCronThreads();
        }

    }

    private boolean isDevPc() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            hostName = "unknown " + ex.getMessage();
        }

        logger.info("running on host: {}", hostName);
        return !prodServers.contains(hostName);
    }

    private void printClassPath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        for (URL url : urls) {
            logger.info(url.getFile());
        }
    }

    private boolean setupDatabase() {
        logger.info("setting up database");
        Database.start();
        logger.info("database ok");
        return true;
    }

    private void setupLogger() {
        int logLevel = appConfig.getInt(LOG_LEVEL_PROP_KEY);
        String logFileProp = appConfig.getString(LOG_FILE_PROP_KEY);
        try {
            feedreader.log.Logger.get().setLevel(feedreader.log.Logger.LogLevels.fromVal(logLevel));
            if (!Environment.isDev()) {
                feedreader.log.Logger.get().setWriter(new FileWriter(logFileProp, true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupOAuth() {
        OAuthConfig.FB_DEV_KEY = appConfig.getString("fb.dev", "see.oauth.properties");
        OAuthConfig.FB_PROD_KEY = appConfig.getString("fb.prod", "see.oauth.properties");
        OAuthConfig.GOOGLE_KEY = appConfig.getString("google", "see.oauth.properties");
        OAuthConfig.LIVE_KEY = appConfig.getString("ms.live", "see.oauth.properties");
    }

    private void startCronThreads() {
        logger.info("starting threads");

        sc = Executors.newSingleThreadScheduledExecutor();

        CronForgotPasswordEmail forgotPwdCron = null;
        CronNewUsersEmail newUsersCron = null;
        try {
            newUsersCron = new CronNewUsersEmail();
            forgotPwdCron = new CronForgotPasswordEmail();
        } catch (Exception e) {
            logger.error("failed to start crons: {}", e, e.getMessage());
            return;
        }
        sc.scheduleAtFixedRate(forgotPwdCron, 0, FeedAppConfig.DELAY_CHECK_FORGOT_PASSWORD, TimeUnit.SECONDS);
        sc.scheduleAtFixedRate(newUsersCron, 0, FeedAppConfig.DELAY_CHECK_NEW_USERS_EMAIL, TimeUnit.SECONDS);
        sc.scheduleAtFixedRate(new CronTimeUtils(), 0, 1, TimeUnit.MINUTES);
        if (FeedAppConfig.FETCH_RUN_START_FETCHING) {
            sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(false), 0,
                    FeedAppConfig.DELAY_FETCH_IN_S, TimeUnit.SECONDS);
        } else {
            logger.info("*** Not starting fetch thread as defined in FeedAppConfig.FETCH_RUN_START_FETCHING.");
        }

        if (FeedAppConfig.FETCH_RUN_START_VALIDATION) {
            sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(true), 0,
                    FeedAppConfig.DELAY_FETCH_IN_S, TimeUnit.SECONDS);
        } else {
            logger.info("*** Not starting fetch thread as defined in FeedAppConfig.FETCH_RUN_START_VALIDATION.");
        }
    }

    public static ServletContextEvent getContext() {
        return servletContext;
    }

    public static String getDownloadXmlPath() {
        return downloadXmlPath;
    }

    public static String getLogFile() {
        return logFile;
    }

}
