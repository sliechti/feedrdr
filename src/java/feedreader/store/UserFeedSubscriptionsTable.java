package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import feedreader.config.Constants;
import feedreader.entities.OPMLEntry;
import feedreader.entities.UserFeedSubscription;
import feedreader.log.Logger;
import feedreader.security.UserSession;
import feedreader.utils.SQLUtils;

public class UserFeedSubscriptionsTable {

    public static final String TABLE = Constants.USER_SUBSCRIPTIONS_TABLE;

    static Class<?> clz = UserFeedSubscriptionsTable.class; // Easier for logging.
    static Connection conn;
    static Statement stmt;

    public static boolean init() {
        conn = Database.getConnection();
        stmt = Database.getStatement();

        Logger.info(clz).log("initialized.").end();
        return true;
    }

    public static void close() {
        Logger.info(clz).log("close()").end();

        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.error(clz).log("closing sql objects ").log(ex.getMessage()).end();
        }
    }

    public static int updateReadMarker(long userId, long xmlId, long marker) {
        String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d AND %s = %d", TABLE,
                DBFields.TIME_READ_MARKER, marker, DBFields.LONG_USER_ID, userId, DBFields.LONG_XML_ID, xmlId);

        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            Logger.error(clz).log(query).log(e.getMessage()).end();
        }

        return -1;
    }

    public enum RetCodes {
        SUBSCRIPTION_ADDED, SUBSCRIPTION_UPDATED, SQL_ERROR
    };

    /**
     * Saves a new subscription. If the folder is unknown, the subscription will be added to the root folder,
     * {@link FeedConstants#DEFAULT_ROOT_FOLDER_ID}
     *
     * @param userId
     * @param sourceId
     * @param entry
     *
     * @return {@link RetCodes#SUBSCRIPTION_ADDED}<br>
     *         {@link RetCodes#SUBSCRIPTION_UPDATED}<br>
     *         {@link RetCodes#SQL_ERROR}
     *
     */
    public static long save(long userId, long sourceId, OPMLEntry entry) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d", 
                    DBFields.LONG_SUBS_ID, 
                    TABLE,
                    DBFields.LONG_USER_ID, userId, 
                    DBFields.LONG_XML_ID, sourceId);
            Logger.debug(clz).log("save (select) ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // TOOD: Implement. // UPDATE
                return rs.getLong(DBFields.LONG_SUBS_ID);
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%s, %d, %d, '%s') RETURNING %s", 
                        TABLE,
                        DBFields.LONG_SUBS_ID, DBFields.LONG_USER_ID, 
                        DBFields.LONG_XML_ID, DBFields.STR_SUBSCRIPTION_NAME, 
                        Database.DEFAULT_KEYWORD, userId, 
                        sourceId, SQLUtils.asSafeString(entry.getTitle()), 
                        DBFields.LONG_SUBS_ID /* returning */);
                Logger.debug(clz).log("save (insert) ").log(query).end();
                rs = stmt.executeQuery(query);

                if (!rs.next()) {
                    return -1;
                }
                return rs.getLong(DBFields.LONG_SUBS_ID);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("save (error) ").log(ex.getMessage()).end();
            return -1;
        }
    }

    public static ArrayList<UserFeedSubscription> get(long userId, long folderId) {
        if (!UserSession.isValid(userId, UserStreamGroupsTable.class)) {
            return null;
        }

        try {
            String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_USER_ID,
                    userId, DBFields.LONG_STREAM_ID, folderId);
            Logger.debug(clz).log(query).end();

            ResultSet rs = stmt.executeQuery(query);

            ArrayList<UserFeedSubscription> ret = new ArrayList<UserFeedSubscription>();
            while (rs.next()) {
                ret.add(getNewUserSubscription(userId, rs));
            }

            return ret;

        } catch (SQLException ex) {
            Logger.error(clz).log("get ").log(userId).log(", error ").log(ex.getMessage()).end();
            return null;
        }
    }

    private static UserFeedSubscription getNewUserSubscription(long userId, ResultSet rs) throws SQLException {
        return new UserFeedSubscription(rs.getLong(DBFields.LONG_SUBS_ID), userId, rs.getLong(DBFields.LONG_XML_ID),
                rs.getLong(DBFields.LONG_STREAM_ID), rs.getString(DBFields.STR_SUBSCRIPTION_NAME));
    }

    public static int removeSubscription(long userId, long subsId) {
        try {
            String query = String.format("DELETE FROM %s WHERE %s = %d",
                    UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS, DBFields.LONG_SUBS_ID, subsId);
            Logger.debugSQL(clz).log(query).end();
            stmt.execute(query);

            query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_SUBS_ID, subsId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            Logger.error(clz).log("removeSubscription ").log(userId).log("/").log(subsId).log(", error ")
                    .log(e.getMessage()).end();
        }

        return 0;
    }

}
