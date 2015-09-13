package feedreader.config;

public class Constants
{
    public static final String ADMIN_USERNAME = "kreon";
    
    public static final String FEED_APP_DB = "postgres";
    public static final String FEED_APP_SCHEMA = "feedreader";
    
    public static final String FEED_SOURCES_TABLE = FEED_APP_SCHEMA + ".feedsources";

    public static final String FEED_SOURCE_CHANNEL_DATA_TABLE = FEED_APP_SCHEMA +  ".feedsourcechanneldata";

    public static final String FEED_SOURCE_IMAGE_DATA_TABLE = FEED_APP_SCHEMA + ".feedsourcechannelimage";

    public static final String FEED_ENTRIES_TABLE = FEED_APP_SCHEMA + ".feedentries";
    
    public static final String FEED_ENTRIES_DATA_TABLE = FEED_APP_SCHEMA + ".feedentriesdata";

    public static final String USER_KEY_VALUE_TABLE  = FEED_APP_SCHEMA + ".userkeyvalues";
    
    public static final String USER_SUBSCRIPTIONS_TABLE = FEED_APP_SCHEMA + ".userfeedsubscriptions";

    public static final String USER_STREAM_GROUPS_TABLE = FEED_APP_SCHEMA + ".userstreamgroups";
    
    public static final String USER_STREAM_GROUP_VIEW_OPTIONS_TABLE = FEED_APP_SCHEMA + ".userstreamgroupviewoptions";
    
    public static final String USER_STREAM_GROUP_FEEDS_SUBS_TABLE = FEED_APP_SCHEMA + ".userstreamgroupfeedsubscription";

    public static final String USERS_SAVED_ENTRIES_TABLE = FEED_APP_SCHEMA + ".usersavedentries";
    
    public static final String USERS_FEED_ENTRIES_INFO_TABLE = FEED_APP_SCHEMA + ".userfeedentriesinfo";
    
    public static final String USERS_TABLE = FEED_APP_SCHEMA + ".users";

    public static final String USER_AUTH_TOKENS = FEED_APP_SCHEMA + ".userauthtokens";
    
    public static final String USER_PROFILES_TABLE = FEED_APP_SCHEMA + ".userprofiles";

    public static final String USER_PROFILES_STREAM_GROUP = FEED_APP_SCHEMA + ".userprofilestreamgroup";
    
    public static final String HTTP_ERRORS_TABLE = FEED_APP_SCHEMA + ".httperrors";
    
    public static final String SOURCE_COLLECTIONS = FEED_APP_SCHEMA + ".sourcecollections";

    public static final String SOURCE_COLLECTIONS_LIST = FEED_APP_SCHEMA + ".sourcecollectionslist";
    
    public static final String SESSION_USERID_FIELD = "ui";
    public static final String SESSION_USER_EMAIL_FIELD = "ue";
    public static final String SESSION_USER_SCREEN_NAME = "us";
    public static final String SESSION_USER_TYPE = "ut";
    public static final String SESSION_EMAIL_VERIFIED = "ev";
    public static final String SESSION_ADMIN_FIELD = "ad";
    public static final String SESSION_OAUTH_FIELD = "oa";
    public static final String SESSION_SELECTED_PROFILE_ID = "sp";
    public static final String SESSION_SELECTED_PROFILE_NAME = "spn";
    public static final String SESSION_SELECTED_PROFILE_COLOR = "spc";
    public static final String SESSION_START_PAGE = "/home.jsp";
    public static final String SESSION_ADMIN_START_PAGE = "/home.jsp";
    public static final String SESSION_ADMIN_LOGIN_PAGE = "/index.jsp";


    public static long DEFAULT_ROOT_FOLDER_ID = 1;
    
    public static long LONG_PROFILE_ID_DEFAULT = 0;
    
    public static String INDEX_PAGE = "/index.jsp";
    
    public static String ENV_DEV_NAME = "DEV";
    public static String ENV_PROD_NAME = "PROD";
    public static String INPUT_EMAIL_NAME = "email";
    public static String INPUT_SCREEN_NAME = "display_name";
    public static String INPUT_PWD_NAME = "pwd";


    public static final String INPUT_REMEMBER_ME = "stay_loggedin";

	public static final int DEFAUT_COOKIE_AGE = 60 * 60 * 24;

	public static final String USER_COOKIE = "rdr";
}
