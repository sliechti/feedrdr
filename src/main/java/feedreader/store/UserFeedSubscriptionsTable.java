package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.OPMLEntry;
import feedreader.entities.UserFeedSubscription;
import feedreader.security.UserSession;
import feedreader.utils.SQLUtils;

public class UserFeedSubscriptionsTable {

    public static final String TABLE = Constants.USER_SUBSCRIPTIONS_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(UserFeedSubscriptionsTable.class);

    public static void close() {
        logger.info("close");
    }

    public static ArrayList<UserFeedSubscription> get(long userId, long folderId) {
        if (!UserSession.isValid(userId)) {
            return null;
        }

        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_USER_ID,
                    userId, DBFields.LONG_STREAM_ID, folderId);

            ResultSet rs = conn.createStatement().executeQuery(query);

            ArrayList<UserFeedSubscription> ret = new ArrayList<UserFeedSubscription>();
            while (rs.next()) {
                ret.add(getNewUserSubscription(userId, rs));
            }

            return ret;

        } catch (SQLException ex) {
            logger.error("get user id {}, error {}", ex, userId, ex.getMessage());
            return null;
        }
    }

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static int removeSubscription(long userId, long subsId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d",
                    UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS, DBFields.LONG_SUBS_ID, subsId);
            conn.createStatement().execute(query);

            query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_SUBS_ID, subsId);
            return conn.createStatement().executeUpdate(query);
        } catch (Exception e) {
            logger.error("remote subscription failed: {}", e, e.getMessage());
        }

        return 0;
    }

    public static long save(long userId, long sourceId, OPMLEntry entry) {
        return save(userId, sourceId, entry.getTitle());
    }

    /**
     * Saves a new subscription. If the folder is unknown, the subscription will be added to the root folder,
     * {@link FeedConstants#DEFAULT_ROOT_FOLDER_ID}
     *
     * @param userId
     * @param sourceId
     * @param entry
     *
     * @return {@link RetCodes#SUBSCRIPTION_ADDED}<br>
     * {@link RetCodes#SUBSCRIPTION_UPDATED}<br>
     * {@link RetCodes#SQL_ERROR}
     *
     */
    public static long save(long userId, long sourceId, String title) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d",
                    DBFields.LONG_SUBS_ID,
                    TABLE,
                    DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_XML_ID, sourceId);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                // TOOD: Implement. // UPDATE
                return rs.getLong(DBFields.LONG_SUBS_ID);
            }
            query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%s, %d, %d, '%s') RETURNING %s",
                    TABLE,
                    DBFields.LONG_SUBS_ID, DBFields.LONG_USER_ID,
                    DBFields.LONG_XML_ID, DBFields.STR_SUBSCRIPTION_NAME,
                    Database.DEFAULT_KEYWORD, userId,
                    sourceId, SQLUtils.asSafeString(title),
                    DBFields.LONG_SUBS_ID /* returning */);
            rs = conn.createStatement().executeQuery(query);

            if (!rs.next()) {
                return -1;
            }
            return rs.getLong(DBFields.LONG_SUBS_ID);
        } catch (SQLException ex) {
            logger.error("save error: {}", ex, ex.getMessage());
            return -1;
        }
    }

    public static int updateReadMarker(long userId, long xmlId, long marker) {
        String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d AND %s = %d", TABLE,
                DBFields.TIME_READ_MARKER, marker, DBFields.LONG_USER_ID, userId, DBFields.LONG_XML_ID, xmlId);

        try (Connection conn = Database.getConnection()) {
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            logger.error("query {} error {}", e, query, e.getMessage());
        }

        return -1;
    }

    private static UserFeedSubscription getNewUserSubscription(long userId, ResultSet rs) throws SQLException {
        return new UserFeedSubscription(rs.getLong(DBFields.LONG_SUBS_ID), userId, rs.getLong(DBFields.LONG_XML_ID),
                rs.getLong(DBFields.LONG_STREAM_ID), rs.getString(DBFields.STR_SUBSCRIPTION_NAME));
    }

    public enum RetCodes {
        SQL_ERROR, SUBSCRIPTION_ADDED, SUBSCRIPTION_UPDATED
    }

}
