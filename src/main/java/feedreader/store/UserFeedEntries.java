package feedreader.store;

import feedreader.config.Constants;
import feedreader.log.Logger;
import feedreader.time.CurrentTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserFeedEntries {

    public static final String TABLE_SAVED = Constants.USERS_SAVED_ENTRIES_TABLE;
    public static final String TABLE_INFO = Constants.USERS_FEED_ENTRIES_INFO_TABLE;

    static Statement stmt;

    static Class<?> clz = UserFeedEntries.class;
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

    public static Object userSaved(long userId, long profileId, long entryId) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d AND %s = %d ",
                    DBFields.LONG_ENTRY_ID, TABLE_SAVED, DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_ENTRY_ID, entryId);
            Logger.debugSQL(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return 1;
            }

            query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %d)", TABLE_SAVED,
                    DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.LONG_ENTRY_ID, DBFields.TIME_SAVED_AT,
                    userId, profileId, entryId, CurrentTime.inGMT());
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);

        } catch (Exception e) {
            if (e.getMessage().contains("duplicate"))
                return 0;

            Logger.error(clz).log("save ").log(e.getMessage()).end();
            return -1;
        }
    }

    public static int removeFromSave(long userId, long profileId, long entryId) {
        try {
            String query = String
                    .format("DELETE FROM %s WHERE %s = %d AND %s = %d AND %s = %d ", TABLE_SAVED,
                            DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_ENTRY_ID,
                            entryId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            Logger.error(clz).log("remove ").log(e.getMessage()).end();
            return -1;
        }
    }

    public static void clicked(long userId, long profileId, long entryId) {
        try {
            String query = String.format("UPDATE %s SET %s = %s + 1, %s = %b WHERE %s = %d AND %s = %d", TABLE_INFO,
                    DBFields.INT_CLICKED, DBFields.INT_CLICKED, DBFields.BOOL_READ, true, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_ENTRY_ID, entryId);
            Logger.debugSQL(clz).log(query).end();

            if (stmt.executeUpdate(query) == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (%d, %d, %d, %d, %d, %b)",
                        TABLE_INFO, DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID,
                        DBFields.TIME_READ_ON, DBFields.INT_CLICKED, DBFields.BOOL_READ, entryId, userId, profileId,
                        CurrentTime.inGMT(), 1, true);
                Logger.debugSQL(clz).log(query).end();
                stmt.execute(query);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("clicked ").log(userId).log("/").log(profileId).log("/").log(entryId).log(", error ")
                    .log(ex.getMessage()).end();
        }
    }

    public static boolean setRead(boolean val, long userId, long profileId, long entryId) {
        Statement stmt = Database.getStatement();
        try {
            String query = String.format("UPDATE %s SET %s = %b WHERE %s = %d AND %s = %d AND %s = %d", TABLE_INFO,
                    DBFields.BOOL_READ, val, DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_ENTRY_ID, entryId);
            Logger.debugSQL(clz).log(query).end();
            int c = stmt.executeUpdate(query);

            if (c == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %b)", TABLE_INFO,
                        DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.BOOL_READ,
                        entryId, userId, profileId, val);
                Logger.debugSQL(clz).log(query).end();
                stmt.execute(query);
            }

            return true;
        } catch (SQLException ex) {
            Logger.error(clz).log("set read ").log(val).log("/").log(userId).log("/").log(profileId).log("/")
                    .log(entryId).log(", error ").log(ex.getMessage()).end();
        }

        return false;
    }

    public static void read(long userId, long profileId, long entryId) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_ENTRY_ID,
                    TABLE_INFO, DBFields.LONG_ENTRY_ID, entryId, DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                query = String.format("UPDATE %s SET %s = %s + 1 WHERE %s = %d AND %s = %d", TABLE_INFO,
                        DBFields.INT_CLICKED, DBFields.INT_CLICKED, DBFields.LONG_PROFILE_ID, profileId,
                        DBFields.LONG_ENTRY_ID, entryId);
                Logger.debugSQL(clz).log(query).end();
                stmt.execute(query);
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (%d, %d, %d, %d, %d)", TABLE_INFO,
                        DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.TIME_READ_ON,
                        DBFields.INT_CLICKED, entryId, userId, profileId, CurrentTime.inGMT(), 1);
                Logger.debugSQL(clz).log(query).end();
                stmt.execute(query);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("clicked ").log(userId).log("/").log(profileId).log("/").log(entryId).log(", error ")
                    .log(ex.getMessage()).end();
        }
    }

    public static int removeAllSavedEntries(long userId, long profileId) {
        try {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_SAVED,
                    DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("removeAllSavedEntries ").log(userId).log(" ").log(profileId).log(", error ")
                    .log(ex.getMessage()).end();
        }

        return -1;
    }

    public static int removeAllEntriesInfo(long userId, long profileId) {
        try {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_INFO, DBFields.LONG_USER_ID,
                    userId, DBFields.LONG_PROFILE_ID, profileId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("removeAllSavedEntries ").log(userId).log(" ").log(profileId).log(", error ")
                    .log(ex.getMessage()).end();
        }

        return -1;
    }

}
