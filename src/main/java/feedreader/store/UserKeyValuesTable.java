package feedreader.store;

import com.restfb.json.JsonObject;

import feedreader.api.v1.JSONFields;
import feedreader.config.Constants;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserKeyValuesTable {

    public static final String TABLE = Constants.USER_KEY_VALUE_TABLE;

    static Class<?> clz = UserKeyValuesTable.class; // Easier for logging.
    static Connection conn;
    static Statement stmt;

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

    public static int save(long userId, long profileId, int key, JsonObject obj) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d AND %s = %d AND %s = %d", TABLE,
                    DBFields.STR_KEY_VALUE, SQLUtils.asSafeString(obj.toString()), DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_USER_ID, userId, DBFields.INT_KEY_NAME, key);
            if (stmt.executeUpdate(query) == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, '%s')", TABLE,
                        DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.INT_KEY_NAME, DBFields.STR_KEY_VALUE,
                        userId, profileId, key, SQLUtils.asSafeString(obj.toString()));
                stmt.execute(query);
            }

            return 1;
        } catch (SQLException ex) {
            Logger.error(clz).log(obj.toString()).log("/").log(userId).log("/").log(profileId).log(", error ")
                    .log(ex.getMessage()).end();
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

        try {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return new JsonObject(rs.getString(1));
            }
        } catch (SQLException e) {
            Logger.error(clz).log(query).log(", error ").log(e.getMessage()).end();
        }

        return emptyJson;
    }

    public static int delete(long userId, long profileId) {
        String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", 
                TABLE, 
                DBFields.LONG_USER_ID, userId, 
                DBFields.LONG_PROFILE_ID, profileId);
        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            Logger.error(clz).log(query).log(", error ").log(e.getMessage()).end();
        }

        return -1;
    }

}
