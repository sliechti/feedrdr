package feedreader.config;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * TODO: Implement. The class could/should be moved to store and have an init
 * method, like the other store classes. When the class is loaded it restores
 * the values from the db to the field variables.
 *
 * Question: What happens when you have 4 tomcats running?
 *
 * Field variables here are not final, because:
 *
 * The values will be loaded from the database.
 *
 * The values in the database will be saved from the admin panel.
 *
 * The admin panel will save to the database and override the values here.
 *
 */
public class FeedAppConfig {

	public static String APP_NAME = "FeedRdr";
	public static String APP_DOMAIN = "http://feedrdr.co";
	public static String APP_VERSION = "v1.1";
	public static String APP_ENV = "DEV,PROD"; // set by the config servlet.
	public static String APP_NAME_URL = "feedrdr.co";
	public static final String DEV_PC = "tux";
	public static String BASE_APP_URL = "/feedreader";
	public static String BASE_APP_URL_EMAIL = "http://feedrdr.co";
	public static String BASE_API_URL = BASE_APP_URL + "/api";
	public static String BASE_ADMIN_URL = "/venus";

	public static String DOWNLOAD_XML_PATH = "";

	// Move to context config. AUtoConfig for PROD and DEV.
	public static int DELAY_CHECK_FORGOT_PASSWORD = 15;
	public static int DELAY_CHECK_NEW_USERS_EMAIL = 15;

	public static int DEFAULT_LOG_LEVEL = 2;
	public static String DEFAULT_LOCALE = Locale.US.toString();
	public static String DEFAULT_TIMEZONE = "GMT";

	public static String DEFAULT_PROFILE_COLOR = "CACACA";
	public static String DEFAULT_PROFILE_NAME = "DEFAULT";

	public static int DEFAULT_READER_ARTICLES_PER_PAGE = 10;
	public static int DEFAULT_READER_NEWS_LINE_TITLE_LEN = 100;

	public static int DEFAULT_API_FETCH_ARTICLES = 15;
	public static String DEFAULT_API_SORT_PUBLICATION_DATE = "DESC";
	public static String DEFAULT_API_SORT_STREAM_GROUP_LIST = "DESC";
	public static String DEFAULT_API_SORT_USER_SUBSCRIPTIONS_LIST = "ASC";
	public static String DEFAULT_API_SORT_PROFILES = "ASC";

	public static boolean XML_GATHER_INFO = false;
	public static boolean XML_SAVE = false;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static String FETCH_USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:20.0) Gecko/20100101 Firefox/20.0";
	/*
	 * This should be changed to whatever interval is defined in the source.
	 * Check RSS/Atom/RDF channel header.
	 */
	public static int FETCH_SOURCE_WAIT_INTERVAL_IN_SECS = 60 * 20;

	public static int FETCH_CONNECTION_TIMEOUT = 5000;

	public static int FETCH_READ_TIMEOUT = 5000;

	public static int FETCH_RETRY_AMOUNT = 10;

	public static String FETCH_ALLOWED_IMAGE_EXTENSIONS = ".*\\.jp.*g.*|.*\\.png.*|.*\\.gif.*";
	public static int MAX_PROFILES_COUNT = 10;
	public static int DEFAULT_API_MAX_CONTENT_LEN = 250;
	public static int MAX_REG_EMAILS_PER_RUN = 10;
	public static int MAX_FORGOTTEN_EMAILS_PER_RUN = 1;
	public static String MAIL_FETCHER_EMAIL = "mailer@feedrdr.co";
	public static String MAIL_BCC_ADDRESS = "steven@feedrdr.co";
	public static String MAIL_REG_FROM = "mailer@feedrdr.co";
	public static String MAIL_FETCHER_TO = "steven@feedrdr.co";
	public static boolean FETCH_FORCE_DELETE = false;
	public static long DELAY_FETCH_IN_S = 3;
	public static long DELAY_VALIDATE_IN_S = 3;
	public static int FETCH_SEND_STATUS_EVERY_MINUTES = 60 * 4; // 4 times a
																// day.
	public static boolean FETCH_RUN_START_FETCHING = false;
	public static boolean FETCH_RUN_START_VALIDATION = false;

	public static final String ENC_KEY_REOVER_EMAIL_CODE = "RECOVEREMAIL";
	public static boolean DEBUG_EMAIL = false;
	public static final int USER_0_VAL = 0;
	public static int USER_0_MAX_DAYS_BACK = -8;
	public static final int USER_1_VAL = 1;
	public static int USER_1_MAX_DAYS_BACK = -15;
	public static final int USER_2_VAL = 2;
	public static int USER_2_MAX_DAYS_BACK = -31;
	public static final int DEFAULT_USER_VAL = USER_0_VAL;
	public static long CACHE_UNREAD_TIME = 60 * 30 * 1000; // 30 min.
	public static long MAX_TIME_GO_BACK = (60 * 60 * 4 * 1000); // 4h, it
																// assumes it
																// takes less
																// than 4h to
																// loop over all
																// feedsources.
}
