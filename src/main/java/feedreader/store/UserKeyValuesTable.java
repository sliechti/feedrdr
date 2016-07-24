package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.json.JsonObject;

import feedreader.api.v1.JSONFields;
import feedreader.config.Constants;
import feedreader.utils.SQLUtils;

public class UserKeyValuesTable {

    public static final String TABLE = Constants.USER_KEY_VALUE_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(UserKeyValuesTable.class);

    public static final int READER_SETTINGS_KEY = 0;
    public static final int VIEW_ALL_SETTINGS = 1;
    public static final int VIEW_SAVED_SETTINGS = 2;
    public static final int VIEW_RECENTLY_READ_SETTINGS = 3;

    public static final JsonObject emptyJson = new JsonObject();
    public static final JsonObject defaultReader = new JsonObject();
    public static final JsonObject defaultGenericSettings = new JsonObject();

    static {
        defaultReader.put(JSONFields.BOOL_SHOW_UNREAD_ONLY, JSONFields.BOOL_SHOW_UNREAD_ONLY_DEFAULT);
        defaultReader.put(JSONFields.INT_SORT_AZ, JSONFields.INT_SORT_AZ_DEFAULT);
        defaultReader.put(JSONFields.INT_SORT_UNREAD, JSONFields.INT_SORT_UNREAD_DEFAULT);

        defaultGenericSettings.put(JSONFields.INT_VIEW_MODE, JSONFields.INT_VIEW_MODE_DEFAULT);
    }

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static int save(long userId, long profileId, int key, JsonObject obj) {
        try (Connection conn = Database.getConnection()){
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d AND %s = %d AND %s = %d", TABLE,
                    DBFields.STR_KEY_VALUE, SQLUtils.asSafeString(obj.toString()), DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_USER_ID, userId, DBFields.INT_KEY_NAME, key);
            if (conn.createStatement().executeUpdate(query) == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, '%s')", TABLE,
                        DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.INT_KEY_NAME, DBFields.STR_KEY_VALUE,
                        userId, profileId, key, SQLUtils.asSafeString(obj.toString()));
                conn.createStatement().execute(query);
            }

            return 1;
        } catch (SQLException ex) {
            logger.error("save {}/{}/{} failed {}", ex, userId, profileId, key, ex.getMessage());
        }

        return -1;
    }

    public static JsonObject get(long userId, long profileId, int key, boolean retDefault) {
        JsonObject o = get(userId, profileId, key);

        if (o.length() == 0) {
            switch (key) {
            case READER_SETTINGS_KEY:
                return defaultReader;

            case VIEW_ALL_SETTINGS:
            case VIEW_SAVED_SETTINGS:
            case VIEW_RECENTLY_READ_SETTINGS:
                return defaultGenericSettings;
            }
        }

        return o;
    }

    public static JsonObject get(long userId, long profileId, int key) {
        String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d AND %s = %d", DBFields.STR_KEY_VALUE,
                TABLE, DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId, DBFields.INT_KEY_NAME, key);

        try (Connection conn = Database.getConnection()){
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return new JsonObject(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.error("get failed: {}, error: {}", e, query, e.getMessage());
        }

        return emptyJson;
    }

    public static int delete(long userId, long profileId) {
        String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d",
                TABLE,
                DBFields.LONG_USER_ID, userId,
                DBFields.LONG_PROFILE_ID, profileId);
        try (Connection conn = Database.getConnection()){
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            logger.error("delete failed {}, error {}", e, query, e.getMessage());
        }

        return -1;
    }

}
