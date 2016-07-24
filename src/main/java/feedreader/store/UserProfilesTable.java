package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.entities.ProfileData;
import feedreader.utils.SQLUtils;

public class UserProfilesTable {

    public static final String TABLE = Constants.USER_PROFILES_TABLE;
    public static final String TABLE_STREAM_GROUPS = Constants.USER_PROFILES_STREAM_GROUP;

    private static final Logger logger = LoggerFactory.getLogger(UserProfilesTable.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static List<ProfileData> getProfiles(long userId) {
        ArrayList<ProfileData> ret = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                ret.add(getProfile(userId, rs.getLong(DBFields.LONG_PROFILE_ID)));
            }
        } catch (SQLException ex) {
            logger.error("get profiles failed: {}", ex, ex.getMessage());
        }
        return ret;
    }

    public static ProfileData getProfile(long userId, long id) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_USER_ID,
                userId, DBFields.LONG_PROFILE_ID, id);
        return getProfile(query);
    }

    public static ProfileData getFirstProfile(long userId) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d LIMIT 1", TABLE, DBFields.LONG_USER_ID, userId);
        return getProfile(query);
    }

    static ProfileData getProfile(String query) {
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (!rs.next()) {
                return ProfileData.NULL;
            }
            return ProfileData.fromRs(rs);
        } catch (SQLException ex) {
            logger.error("failed {}, error {}", ex, query, ex.getMessage());
            return ProfileData.NULL;
        }
    }

    public static int removeStreamGroupFromProfile(long streamId, long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_STREAM_GROUPS,
                    DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_STREAM_ID, streamId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static int removeAllStreamGroupFromProfile(long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE_STREAM_GROUPS, DBFields.LONG_PROFILE_ID,
                    profileId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static boolean addStreamToProfile(long streamId, long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_PROFILE_ID,
                    TABLE_STREAM_GROUPS, DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_STREAM_ID, streamId);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next())
                return true;

            query = String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d) RETURNING %s", TABLE_STREAM_GROUPS,
                    DBFields.LONG_PROFILE_ID, DBFields.LONG_STREAM_ID, profileId, streamId, DBFields.LONG_PROFILE_ID);
            rs = conn.createStatement().executeQuery(query);

            if (rs.next())
                return true;
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return false;
    }

    public static void addStreamToAllProfiles(long userId, long streamId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %S WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);

            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                addStreamToProfile(streamId, rs.getLong(DBFields.LONG_PROFILE_ID));
            }
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }
    }

    public static void removeStreamGroupFromUserId(long streamId, long userId) {
        List<ProfileData> profiles = getProfiles(userId);
        for (ProfileData p : profiles) {
            removeStreamGroupFromProfile(streamId, p.getProfileId());
        }
    }

    public static boolean streamGroupKnown(long streamId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_STREAM_GROUPS,
                    DBFields.LONG_STREAM_ID, streamId);
            return conn.createStatement().executeQuery(query).next();
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return false;
    }

    public static long addProfile(long userId, String profileName, String profileColor) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%s, %d, '%s', '%s') RETURNING %s",
                    TABLE, DBFields.LONG_PROFILE_ID, DBFields.LONG_USER_ID, DBFields.STR_PROFILE_NAME,
                    DBFields.STR_COLOR, Database.DEFAULT_KEYWORD, userId, SQLUtils.asSafeString(profileName),
                    SQLUtils.asSafeString(profileColor), DBFields.LONG_PROFILE_ID);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return rs.getLong(DBFields.LONG_PROFILE_ID);
            }

            return -1;
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
            return -1;
        }
    }

    public static int getProfileCount(long userId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getInt(Database.COUNT_KEYWORD);
            }
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static long createDefaultProfile(long userId) {
        try (Connection conn = Database.getConnection()) {
            int c = getProfileCount(userId);
            if (c != 0) {
                logger.warn("Asked to create default profile for user {} with profile count {}", userId, c);
                return 0;
            }

            String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) "
                    + "VALUES (%s, %d, '%s', '%s', %b) RETURNING %s", TABLE, DBFields.LONG_PROFILE_ID,
                    DBFields.LONG_USER_ID, DBFields.STR_PROFILE_NAME, DBFields.STR_PROFILE_COLOR,
                    DBFields.BOOL_DEFAULT, Database.DEFAULT_KEYWORD, userId, FeedAppConfig.DEFAULT_PROFILE_NAME,
                    FeedAppConfig.DEFAULT_PROFILE_COLOR, true, DBFields.LONG_PROFILE_ID);

            ResultSet rs = conn.createStatement().executeQuery(query);
            if (!rs.next()) {
                return 0;
            }

            return rs.getLong(DBFields.LONG_PROFILE_ID);
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return 0;
    }

    public static int save(long userId, ProfileData data) {
        try (Connection conn = Database.getConnection()) {

            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d AND %s = %d", TABLE,
                    DBFields.STR_PROFILE_NAME, SQLUtils.asSafeString(data.getName()), DBFields.STR_PROFILE_COLOR,
                    SQLUtils.asSafeString(data.getColor()), DBFields.LONG_PROFILE_ID, data.getProfileId(),
                    DBFields.LONG_USER_ID, userId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return 0;
    }

    public static int delete(long userId, long profileId) {
        try (Connection conn = Database.getConnection()) {

            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_USER_ID, userId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("query failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static List<Long> validate(long userId, List<Long> idsList) {
        StringBuilder sb = new StringBuilder();
        for (Long l : idsList) {
            sb.append(l).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s "
                    + "WHERE %s = %d AND %s IN (" + sb.toString() + ")",
                    DBFields.LONG_PROFILE_ID,
                    TABLE,
                    DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_PROFILE_ID);
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<Long> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(rs.getLong(1));
            }
            return ret;
        } catch (SQLException e) {
            logger.error("query failed: {}", e, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * @param profiles comma separated list of profiles, useful when working with api queryparam.
     * @throws SQLException
     */
    public static List<Long> validate(long userId, String profiles) throws SQLException {
        String[] ids = profiles.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String idStr : ids) {
            try (Connection conn = Database.getConnection()) {
                idList.add(Long.parseLong(idStr));
            } catch (NumberFormatException e) {
                /* noop */
            }
        }
        return validate(userId, idList);
    }

}
