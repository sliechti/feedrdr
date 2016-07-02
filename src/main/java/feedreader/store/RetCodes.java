package feedreader.store;

/**
 * Positive, non errors, may be discarded.
 *
 * Negative, errors. Should check.
 *
 */
public class RetCodes
{
    public static final int SOURCE_ALREADY_QUEUED = 8;
    public static final int STREAM_GROUP_KNOWN = 7;
    public static final int STREAM_GROUP_ADDED = 6;
    public static final int SUBSCRIPTION_ADDED = 5;
    public static final int SUBSCRIPTION_UPDATED = 4;
    public static final int SOURCE_QUEUED = 3;
    public static final int SOURCE_ALREADY_KNOWN = 2;

    public static final int OK = 1;

    public static final int INVALID_USER_ID = -2;
    public static final int UNACK_WRITE_ERROR = -3;
    public static final int ACK_WRITE_ERROR = -4;
    public static final int SQL_ERROR = -5;
}
