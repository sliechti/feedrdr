package feedreader.main;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.config.OAuthConfig;
import feedreader.cron.CronFetchNews;
import feedreader.cron.CronForgotPasswordEmail;
import feedreader.cron.CronNewUsersEmail;
import feedreader.cron.CronResendRegEmail;
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
    private static final String DOWNLOAD_XML_FILES = "download_xml_files";
    private static final String DOWNLOAD_XML_PATH_PRO_KEY = "download_xml_path";
    private static String downloadXmlPath = "";
    private static final String INTERVAL_FETCH_PROP_KEY = "interval_fetch_seconds";
    private static final String LOG_FILE_PROP_KEY = "log_file";
    private static final String LOG_LEVEL_PROP_KEY = "log_level";
    private static String logFile = "";
    private static final Logger logger = LoggerFactory.getLogger(AppContextInit.class);
    private static ServletContextEvent servletContext;

    private ApplicationConfig appConfig;
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
        appConfig = ApplicationConfig.instance();

        logger.info("is local environment : {}", appConfig.isLocal());
        printClassPath();

        setupLogger();
        setupOAuth();

        downloadXmlPath = appConfig.getString(DOWNLOAD_XML_PATH_PRO_KEY);
        FeedAppConfig.DOWNLOAD_XML_FILES = appConfig.getBoolean(DOWNLOAD_XML_FILES, false);
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

        FeedAppConfig.DOWNLOAD_XML_PATH = downloadXmlPath;
        FeedAppConfig.DELAY_FETCH_IN_S = delayFetch;
        FeedAppConfig.BASE_APP_URL = baseUrl;
        FeedAppConfig.BASE_APP_URL_EMAIL = baseUrlEmail;
        FeedAppConfig.BASE_ADMIN_URL = baseAdminUrl;

        if (setupDatabase()) {
            startCronThreads();
        }

        try {
            logger.info("{}", FeedAppConfig.printAllFields());
        } catch (Exception e) {
            logger.error("failed to read feed app config values: {}", e, e.getMessage());
        }
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
            feedreader.log.Logger.get().setWriter(new FileWriter(logFileProp, true));
        } catch (IOException e) {
            logger.error("failed to set legacy logger: {}", e, e.getMessage());
        }
    }

    private void setupOAuth() {
        OAuthConfig.FB_KEY = appConfig.getString("facebook", "see.oauth.properties");
        OAuthConfig.GOOGLE_KEY = appConfig.getString("google", "see.oauth.properties");
        OAuthConfig.LIVE_KEY = appConfig.getString("ms.live", "see.oauth.properties");
    }

    private void startCronThreads() {
        logger.info("starting cron threads");

        sc = Executors.newSingleThreadScheduledExecutor();
        sc.scheduleAtFixedRate(new CronTimeUtils(), 0, 1, TimeUnit.MINUTES);

        boolean startPwdCron = appConfig.getBoolean("cron_start_email_forgotpwd", false);
        logger.info("start cron: {}={}", CronForgotPasswordEmail.class.getSimpleName(), startPwdCron);
        if (startPwdCron) {
            try {
                sc.scheduleAtFixedRate(new CronForgotPasswordEmail(),
                        0, FeedAppConfig.DELAY_CHECK_FORGOT_PASSWORD, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("failed to start forgot password cron: {}", e, e.getMessage());
            }
        }

        boolean startNewUsersEmailCron = appConfig.getBoolean("cron_start_email_newusers", false);
        logger.info("start cron: {}={}", CronNewUsersEmail.class.getSimpleName(), startNewUsersEmailCron);
        if (startNewUsersEmailCron) {
            try {
                sc.scheduleAtFixedRate(new CronNewUsersEmail(),
                        0, FeedAppConfig.DELAY_CHECK_NEW_USERS_EMAIL, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("failed to start new users email cron: {}", e, e.getMessage());
                return;
            }
        }

        boolean cronStartFetchNews = appConfig.getBoolean("cron_start_fetch_news", false);
        logger.info("start cron: {}={}", CronFetchNews.class.getSimpleName(), cronStartFetchNews);
        if (cronStartFetchNews) {
            sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(false), 0,
                    FeedAppConfig.DELAY_FETCH_IN_S, TimeUnit.SECONDS);
        }

        boolean cronStartValidateNews = appConfig.getBoolean("cron_start_validate_news", false);
        logger.info("start cron: {}={}", CronFetchNews.class.getSimpleName(), cronStartValidateNews);
        if (cronStartValidateNews) {
            sc.scheduleAtFixedRate(CronFetchNews.fetchInstance(true), 0,
                    FeedAppConfig.DELAY_FETCH_IN_S, TimeUnit.SECONDS);
        }
        
        boolean startResendRegEmailCron = appConfig.getBoolean("cron_start_email_resend_reg", false);
        logger.info("start cron: {}={}", CronResendRegEmail.class.getSimpleName(), startResendRegEmailCron);
        if (startResendRegEmailCron) {
            try {
            	sc.scheduleAtFixedRate(new CronResendRegEmail(), 0,
                    FeedAppConfig.DELAY_CHECK_REG_EMAIL, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                logger.error("failed to start resend registration email cron: {}", e, e.getMessage());
            }
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
