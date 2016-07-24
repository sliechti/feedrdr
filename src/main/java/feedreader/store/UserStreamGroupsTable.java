package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.entities.StreamGroup;
import feedreader.security.UserSession;
import feedreader.time.CurrentTime;
import feedreader.utils.SQLUtils;

public class UserStreamGroupsTable {

    public static final String TABLE = Constants.USER_STREAM_GROUPS_TABLE;
    public static final String TABLE_VIEWS = Constants.USER_STREAM_GROUP_VIEW_OPTIONS_TABLE;
    public static final String TABLE_STREAM_SUBSCRIPTIONS = Constants.USER_STREAM_GROUP_FEEDS_SUBS_TABLE;

    private static final Logger logger = LoggerFactory.getLogger(UserStreamGroupsTable.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static long save(long userId, String streamName) {
        if (!UserSession.isValid(userId)) {
            return RetCodes.INVALID_USER_ID;
        }

        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s=%d AND %s='%s'", DBFields.LONG_STREAM_ID, TABLE,
                    DBFields.LONG_USER_ID, userId, DBFields.STR_STREAM_NAME, SQLUtils.asSafeString(streamName));
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                // update
                return rs.getLong(DBFields.LONG_STREAM_ID);
            }
            // insert
            query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%s, '%s', %d) RETURNING %s", TABLE,
                    DBFields.LONG_STREAM_ID, DBFields.STR_STREAM_NAME, DBFields.LONG_USER_ID,
                    Database.DEFAULT_KEYWORD, SQLUtils.asSafeString(streamName), userId, DBFields.LONG_STREAM_ID);
            rs = conn.createStatement().executeQuery(query);
            if (!rs.next())
                return -1;
            return rs.getLong(DBFields.LONG_STREAM_ID);
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
            return RetCodes.SQL_ERROR;
        }
    }

    public static ResultSet get(Connection conn, long profileId, boolean view) throws SQLException {
        String query = String.format("SELECT * from %s AS t1 INNER JOIN %s AS t2 ON " + " t1.%s = t2.%s ", TABLE,
                UserProfilesTable.TABLE_STREAM_GROUPS, DBFields.LONG_STREAM_ID, DBFields.LONG_STREAM_ID);

        if (view) {
            query += String.format(" LEFT JOIN %s AS t3 " + "ON t1.%s = t3.%s ", TABLE_VIEWS, DBFields.LONG_STREAM_ID,
                    DBFields.LONG_V_STREAM_ID);
        }

        query += String.format("WHERE t2.%s = %d ORDER BY t1.%s %s", DBFields.LONG_PROFILE_ID, profileId,
                DBFields.LONG_STREAM_ID, FeedAppConfig.DEFAULT_API_SORT_STREAM_GROUP_LIST);

        return conn.createStatement().executeQuery(query);
    }

    public static List<StreamGroup> get(long userId) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE, DBFields.LONG_USER_ID, userId);
        return get(userId, query);
    }

    static List<StreamGroup> get(long userId, String query) {
        if (!UserSession.isValid(userId)) {
            return null;
        }

        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(query);

            ArrayList<StreamGroup> ret = new ArrayList<StreamGroup>();
            while (rs.next()) {
                ret.add(getStreamGroup(userId, rs));
            }

            return ret;

        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
            return null;
        }
    }

    private static StreamGroup getStreamGroup(long userId, ResultSet rs) throws SQLException {
        return new StreamGroup(userId, rs.getLong(DBFields.LONG_STREAM_ID), rs.getString(DBFields.STR_STREAM_NAME));
    }

    public static int rename(long userId, long streamId, String streamName) {
        if (streamName == null || streamName.isEmpty())
            return 0;

        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d AND %s = %d", TABLE,
                    DBFields.STR_STREAM_NAME, SQLUtils.asSafeString(streamName), DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_STREAM_ID, streamId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static boolean addSubscriptionToStream(long streamId, long subsId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_STREAM_ID,
                    TABLE_STREAM_SUBSCRIPTIONS, DBFields.LONG_STREAM_ID, streamId, DBFields.LONG_SUBS_ID, subsId);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next())
                return true;

            query = String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d) RETURNING %s", TABLE_STREAM_SUBSCRIPTIONS,
                    DBFields.LONG_STREAM_ID, DBFields.LONG_SUBS_ID, streamId, subsId, DBFields.LONG_STREAM_ID);
            rs = conn.createStatement().executeQuery(query);

            if (rs.next())
                return true;
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return false;
    }

    public static int removeStreamGroup(long streamId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE, DBFields.LONG_STREAM_ID, streamId);
            int c = conn.createStatement().executeUpdate(query);
            if (c > 0) {
                removeStreamSubscriptions(streamId);
            }

            return c;
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return 0;
    }

    public static int removeStreamSubscriptions(long streamId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE_STREAM_SUBSCRIPTIONS,
                    DBFields.LONG_STREAM_ID, streamId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return 0;
    }

    public static int saveView(long streamId, int viewId, int filterBy, int sort, long count) {
        try (Connection conn = Database.getConnection()) {
            String query;

            if (count >= 0) {
                query = String.format("UPDATE %s SET %s = %d, %s = %d WHERE %s = %d", TABLE, DBFields.LONG_GR_UNREAD,
                        count, DBFields.TIME_GR_UNREAD, CurrentTime.inGMT(), DBFields.LONG_STREAM_ID, streamId);
                conn.createStatement().execute(query);
            }

            query = String.format("UPDATE %s SET %s = %d, %s = %d, %s = %d WHERE %s = %d", TABLE_VIEWS,
                    DBFields.SHORT_VIEW_MODE, viewId, DBFields.SHORT_FILTER_BY, filterBy, DBFields.SHORT_SORT_BY, sort,
                    DBFields.LONG_V_STREAM_ID, streamId);

            int c = conn.createStatement().executeUpdate(query);
            if (c == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %d)", TABLE_VIEWS,
                        DBFields.SHORT_VIEW_MODE, DBFields.LONG_V_STREAM_ID, DBFields.SHORT_FILTER_BY,
                        DBFields.SHORT_SORT_BY, viewId, streamId, filterBy, sort);
                return conn.createStatement().executeUpdate(query);
            }
            return c;
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static int updateUnreadCount(long userId, long streamId, long count) {
        String query = "select feedreader.updatestreamunreadcount(" + userId + ", " + streamId + ", " + count + ", "
                + CurrentTime.inGMT() + ")";
        try (Connection conn = Database.getConnection()) {
            return (conn.createStatement().execute(query) ? 1 : 0);
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    /**
     * @param userId
     * @param streamId
     * @param time
     */
    public static int setMaxTime(long userId, long streamId, long time) {
        // time = time - FeedAppConfig.MAX_TIME_GO_BACK; // 4h hours back.
        //
        // String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d AND %s = %d",
        // TABLE,
        // DBFields.TIME_GR_MAX_TIME, time,
        // DBFields.LONG_STREAM_ID, streamId,
        // DBFields.LONG_USER_ID, userId);
        //
        // try (Connection conn = Database.getConnection()) {
        // return stmt.executeUpdate(query);
        // } catch (SQLException e) {
        // Logger.error(clz).log("error ").log(e.getMessage()).end();
        // }

        return 0;
    }

    public static long getMaxTime(long streamId) {
        String query = "SELECT " + DBFields.TIME_GR_MAX_TIME + " FROM " + TABLE + " WHERE " + DBFields.LONG_STREAM_ID
                + " = " + streamId;
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next())
                return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("failed: {}", e, e.getMessage());
        }

        return -1;
    }

    /**
     * This method is created to getStream Id if stream with streamName exists.
     *
     * @param userId
     * @param streamName
     * @return
     */
    public static boolean isStreamExist(long userId, String streamName) {
        if (!UserSession.isValid(userId)) {
            return false;
        }

        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s=%d AND %s ILIKE '%s'", DBFields.LONG_STREAM_ID,
                    TABLE,
                    DBFields.LONG_USER_ID, userId, DBFields.STR_STREAM_NAME, SQLUtils.asSafeString(streamName));
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
            return false;
        }
    }

}
