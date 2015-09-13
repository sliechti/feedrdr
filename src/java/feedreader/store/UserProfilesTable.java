package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.entities.ProfileData;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;

public class UserProfilesTable {

    public static final String TABLE = Constants.USER_PROFILES_TABLE;
    public static final String TABLE_STREAM_GROUPS = Constants.USER_PROFILES_STREAM_GROUP;

    static Statement stmt;

    static Class<?> clz = UserProfilesTable.class; // Easier for logging.
    static Connection conn;

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

    public static List<ProfileData> getProfiles(long userId) {
        ArrayList<ProfileData> ret = new ArrayList<>();
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);
            Logger.debug(clz).log("getProfiles ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                ret.add(getProfile(userId, rs.getLong(DBFields.LONG_PROFILE_ID)));
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("getProfiles error, ").log(ex.getMessage()).end();
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
        try {
            Logger.debug(clz).log(query).end();
            ResultSet rs = UsersTable.stmt.executeQuery(query);
            if (!rs.next()) {
                return ProfileData.NULL;
            }
            return ProfileData.fromRs(rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("getProfile query ").log(ex.getMessage()).end();
            return ProfileData.NULL;
        }
    }

    public static int removeStreamGroupFromProfile(long streamId, long profileId) {
        try {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_STREAM_GROUPS,
                    DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_STREAM_ID, streamId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("deleteStream error, ").log(ex.getMessage()).end();
        }

        return -1;
    }

    public static int removeAllStreamGroupFromProfile(long profileId) {
        try {
            String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE_STREAM_GROUPS, DBFields.LONG_PROFILE_ID,
                    profileId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("deleteStream error, ").log(ex.getMessage()).end();
        }

        return -1;
    }

    public static boolean addStreamToProfile(long streamId, long profileId) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_PROFILE_ID,
                    TABLE_STREAM_GROUPS, DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_STREAM_ID, streamId);
            Logger.debug(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next())
                return true;

            query = String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d) RETURNING %s", TABLE_STREAM_GROUPS,
                    DBFields.LONG_PROFILE_ID, DBFields.LONG_STREAM_ID, profileId, streamId, DBFields.LONG_PROFILE_ID);
            Logger.debug(clz).log(query).end();
            rs = stmt.executeQuery(query);

            if (rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.error(clz).log("addStreamToProfile ").log(streamId).log(" / ").log(profileId).log(" error ")
                    .log(ex.getMessage()).end();
        }

        return false;
    }

    public static void addStreamToAllProfiles(long userId, long streamId) {
        try {
            String query = String.format("SELECT %s FROM %S WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);
            Logger.debug(clz).log(query).end();

            Statement tempStmt = conn.createStatement();
            ResultSet rs = tempStmt.executeQuery(query);

            while (rs.next()) {
                addStreamToProfile(streamId, rs.getLong(DBFields.LONG_PROFILE_ID));
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("addStreamToAllProfiles ").log(userId).log("/").log(streamId).log(" error ")
                    .log(ex.getMessage()).end();
        }
    }

    public static void removeStreamGroupFromUserId(long streamId, long userId) {
        List<ProfileData> profiles = getProfiles(userId);
        for (ProfileData p : profiles) {
            removeStreamGroupFromProfile(streamId, p.getProfileId());
        }
    }

    public static boolean streamGroupKnown(long streamId) {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_STREAM_GROUPS,
                    DBFields.LONG_STREAM_ID, streamId);
            Logger.debugSQL(clz).log(query).end();

            return stmt.executeQuery(query).next();
        } catch (SQLException ex) {
            Logger.error(clz).log("streamGroupKnown ").log(streamId).log(", error ").log(ex.getMessage()).end();
        }

        return false;
    }

    public static long addProfile(long userId, String profileName, String profileColor) {
        try {
            String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%s, %d, '%s', '%s') RETURNING %s",
                    TABLE, DBFields.LONG_PROFILE_ID, DBFields.LONG_USER_ID, DBFields.STR_PROFILE_NAME,
                    DBFields.STR_COLOR, Database.DEFAULT_KEYWORD, userId, SQLUtils.asSafeString(profileName),
                    SQLUtils.asSafeString(profileColor), DBFields.LONG_PROFILE_ID);
            Logger.debugSQL(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getLong(DBFields.LONG_PROFILE_ID);
            }

            return -1;
        } catch (SQLException ex) {
            Logger.error(clz).log("addProfile ").log(userId).log("/").log(profileName).log("/").log(profileColor)
                    .log(ex.getMessage()).end();
            return -1;
        }
    }

    public static int getProfileCount(long userId) {
        try {
            String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = %d", DBFields.LONG_PROFILE_ID, TABLE,
                    DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(Database.COUNT_KEYWORD);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("getProfileCount ").log(userId).log(" ").log(ex.getMessage());
        }

        return -1;
    }

    public static long createDefaultProfile(long userId) {
        try {
            int c = getProfileCount(userId);
            if (c != 0) {
                Logger.warning(clz).log("Asked to create default profile for user ").log(userId)
                        .log(" with profile count ").log(c).end();
                return 0;
            }

            String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) "
                    + "VALUES (%s, %d, '%s', '%s', %b) RETURNING %s", TABLE, DBFields.LONG_PROFILE_ID,
                    DBFields.LONG_USER_ID, DBFields.STR_PROFILE_NAME, DBFields.STR_PROFILE_COLOR,
                    DBFields.BOOL_DEFAULT, Database.DEFAULT_KEYWORD, userId, FeedAppConfig.DEFAULT_PROFILE_NAME,
                    FeedAppConfig.DEFAULT_PROFILE_COLOR, true, DBFields.LONG_PROFILE_ID);

            ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return 0;
            }

            return rs.getLong(DBFields.LONG_PROFILE_ID);
        } catch (SQLException ex) {
            Logger.error(clz).log("createDefaultProfile ").log(userId).log(", error ").log(ex.getMessage()).end();
        }

        return 0;
    }

    public static int save(long userId, ProfileData data) {
        try {

            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d AND %s = %d", TABLE,
                    DBFields.STR_PROFILE_NAME, SQLUtils.asSafeString(data.getName()), DBFields.STR_PROFILE_COLOR,
                    SQLUtils.asSafeString(data.getColor()), DBFields.LONG_PROFILE_ID, data.getProfileId(),
                    DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log("save ").log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("save ").log(userId).log(" ").log(data).log(", error ").log(ex.getMessage()).end();
        }

        return 0;
    }

    public static int delete(long userId, long profileId) {
        try {

            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("delete uid ").log(userId).log(", profile id ").log(profileId).log(", errror ").log(ex.getMessage())
                    .end();
        }

        return -1;
    }

    public static List<Long> validate(long userId, List<Long> idsList) {
        StringBuilder sb = new StringBuilder();
        for (Long l : idsList) {
            sb.append(l).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length()-1);
        }
        try {
            String query = String.format("SELECT %s FROM %s "
                + "WHERE %s = %d AND %s IN (" + sb.toString() + ")",
                DBFields.LONG_PROFILE_ID,
                TABLE,
                DBFields.LONG_USER_ID, userId,
                DBFields.LONG_PROFILE_ID);
            ResultSet rs = stmt.executeQuery(query);
            List<Long> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(rs.getLong(1));
            }
            return ret;
        } catch (SQLException e) {
            Logger.error(clz).log("validate userid ").log(userId).log(", ids ").log(sb.toString()).log(", ex ").log(e.getMessage()).end();
            return Collections.emptyList();
        }
    }

    /**
     * @param profiles comma separated list of profiles, useful when working with api queryparam.
     */
    public static List<Long> validate(long userId, String profiles) {
        String[] ids = profiles.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String idStr : ids) {
            try {
                idList.add(Long.parseLong(idStr));
            } catch (NumberFormatException e) {
                /* noop */
            }
        }
        return validate(userId, idList);
    }

}
