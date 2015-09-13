package feedreader.store;

/**
 * Definition of application wide tags.
 */
public class FeedTags
{
    public static final String TAG_XML_URL = "s_xmlUrl";
    public static final String TAG_URL = "s_url";
    public static final String TAG_USER_ID = "l_userId";
    public static final String TAG_USERNAME = "s_username";
    public static final String TAG_PASSWORD = "s_password";
    public static final String TAG_ERROR_COUNT = "i_errorCount";
    public static final int    DEFAULT_FOR_ERROR_COUNT = 0;
    public static final String TAG_TITLE = "s_title";

    public static final String TAG_CONTENT = "txt_content";
    public static final String DEFAULT_FOR_TAG_CONTENT = "";

    public static final String TAG_AUTHOR = "s_author";
    public static final String DEFAULT_FOR_TAG_AUTHOR = "none";

    public static final String TAG_PUBLICATION_DATE = "t_pubDate";
    public static final String TAG_FOLDER_ID = "s_folderId";
    public static final String TAG_FOLDER_NAME = "s_folderName";
    public static final String TAG_FOLDER_PARENT_ID = "l_folderParentId";

    public static final String TAG_CHECKED_AT = "t_checkedAt";
    public static final int    DEFAULT_FOR_CHECKED_AT = 0;

    public static final String TAG_NEXT_CHECK_AT = "t_nextCheckAt";
    public static final String TAG_PUBLISHED_AT = "t_publishedAt";
    public static final String TAG_DISCOVERED_AT = "t_discoveredAt";
    public static final String TAG_FB_LIKES = "i_fbLikes";
    public static final String TAG_FB_COMMENTS = "i_fbComments";
    public static final String TAG_FUSR_LIKES = "i_fusrLikes";
    public static final String TAG_FUSR_COMMENTS = "i_fusrComments";
    public static final String TAG_GOOG_PLUSES = "i_googPluses";
    public static final String TAG_GOOG_COMMENTS ="i_googComments";
    public static final String TAG_FB_SHARES = "i_fbShares";
    public static final String TAG_FB_CLICKS = "i_fbClicks";
    public static final String TAG_SHOW_FEATURED = "b_showFeatured";
    public static final String TAG_ARTICLES_PER_PAGE = "i_articlesPerPage";
    /** For user subscriptions. Defines if the source may be used as a publishing source. */
    public static final String TAG_PUBLISH_TO_WALL = "b_publishToWall";
    public static final String TAG_ADDED_AT = "t_addedAt";

    public static final String TAG_DESCRIPTION = "s_desc";
    public static final String TAG_LINK = "s_link";
    public static final String TAG_LANGUAGE = "s_lang";
    public static final String TAG_INTERVAL_IN_MINS = "i_intInMins";
    public static final String TAG_WIDTH = "i_width";
    public static final String TAG_HEIGHT = "i_height";

    // -------------------- ENUM TAGS ---------------------------------------------------------------------------------
    // keep below this line for now.
    // ----------------------------------------------------------------------------------------------------------------

    /** For reader listings, the default sort */
    public static final String TAG_ENUM_SORT_BY = "e_sortBy";

    /**
     * @see FeedTags#PUBLISH_DATE_NOW
     * @see FeedTags#PUBLISH_DATE_DISCOVERY_DATE
     * @see FeedTags#PUBLISH_DATE_ARTICLES_PUBDATE
     * @see FeedTags#DEFAULT_FOR_ENUM_PUBLISH_WITH_DATE
     */
    public static final String TAG_ENUM_PUBLISH_WITH_DATE = "e_publishWithDate";

    // -------------------- ENUM VALUES ---------------------------------------------------------------------------------
    // keep below this line for now.
    // ----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * @see co.fusr.apps.feeds.utils.FeedTags#TAG_ENUM_PUBLISH_WITH_DATE
     * @see co.fusr.apps.feeds.utils.FeedTags#DEFAULT_FOR_ENUM_PUBLISH_WITH_DATE
     */
    public static final int PUBLISH_DATE_NOW = 0;
    /** @see #PUBLISH_DATE_NOW */
    public static final int PUBLISH_DATE_DISCOVERY_DATE = 1;
    /** @see #PUBLISH_DATE_NOW */
    public static final int PUBLISH_DATE_ARTICLES_PUBDATE = 2;

    /** Default publish with date value = DATE_NOW
     * @see #PUBLISH_DATE_NOW */
    public static final int DEFAULT_FOR_ENUM_PUBLISH_WITH_DATE = PUBLISH_DATE_NOW;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int SORT_BY_PUBLICATION_DATE = 0;
    public static final int SORT_BY_DISCOVERY_DATE = 1;
    public static final int SORT_BY_POPULARITY = 2;

    public static final int DEFAULT_FOR_ENUM_SORT_BY = SORT_BY_PUBLICATION_DATE;
    // -----------------------------------------------------------------------------------------------------------------

}
