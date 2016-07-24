package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.time.CurrentTime;

public class UserFeedEntries {

    public static final String TABLE_SAVED = Constants.USERS_SAVED_ENTRIES_TABLE;
    public static final String TABLE_INFO = Constants.USERS_FEED_ENTRIES_INFO_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(UserFeedEntries.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static Object userSaved(long userId, long profileId, long entryId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d AND %s = %d ",
                    DBFields.LONG_ENTRY_ID, TABLE_SAVED, DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_ENTRY_ID, entryId);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return 1;
            }

            query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %d)", TABLE_SAVED,
                    DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.LONG_ENTRY_ID, DBFields.TIME_SAVED_AT,
                    userId, profileId, entryId, CurrentTime.inGMT());
            return conn.createStatement().executeUpdate(query);

        } catch (Exception e) {
            if (e.getMessage().contains("duplicate")) {
                return 0;
            }
            logger.error("user saved {}, {}, entryId {}, error: {}", e, userId, profileId, entryId, e.getMessage());
            return -1;
        }
    }

    public static int removeFromSave(long userId, long profileId, long entryId) {
        try (Connection conn = Database.getConnection()) {
            String query = String
                    .format("DELETE FROM %s WHERE %s = %d AND %s = %d AND %s = %d ", TABLE_SAVED,
                            DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId, DBFields.LONG_ENTRY_ID,
                            entryId);
            return conn.createStatement().executeUpdate(query);
        } catch (Exception e) {
            logger.error("remove from save error: {}", e, e.getMessage());
            return -1;
        }
    }

    public static void clicked(long userId, long profileId, long entryId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = %s + 1, %s = %b WHERE %s = %d AND %s = %d", TABLE_INFO,
                    DBFields.INT_CLICKED, DBFields.INT_CLICKED, DBFields.BOOL_READ, true, DBFields.LONG_PROFILE_ID,
                    profileId, DBFields.LONG_ENTRY_ID, entryId);

            if (conn.createStatement().executeUpdate(query) == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (%d, %d, %d, %d, %d, %b)",
                        TABLE_INFO, DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID,
                        DBFields.TIME_READ_ON, DBFields.INT_CLICKED, DBFields.BOOL_READ, entryId, userId, profileId,
                        CurrentTime.inGMT(), 1, true);
                conn.createStatement().execute(query);
            }
        } catch (SQLException ex) {
            logger.error("clicked: {}/{}/{}, error: {}", ex, userId, profileId, entryId, ex.getMessage());
        }
    }

    public static boolean setRead(boolean val, long userId, long profileId, long entryId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = %b WHERE %s = %d AND %s = %d AND %s = %d", TABLE_INFO,
                    DBFields.BOOL_READ, val, DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_ENTRY_ID, entryId);
            int c = conn.createStatement().executeUpdate(query);

            if (c == 0) {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %b)", TABLE_INFO,
                        DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.BOOL_READ,
                        entryId, userId, profileId, val);
                conn.createStatement().execute(query);
            }

            return true;
        } catch (SQLException ex) {
            logger.error("set read error: {}", ex, ex.getMessage());
        }

        return false;
    }

    public static void read(long userId, long profileId, long entryId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_ENTRY_ID,
                    TABLE_INFO, DBFields.LONG_ENTRY_ID, entryId, DBFields.LONG_PROFILE_ID, profileId,
                    DBFields.LONG_USER_ID, userId);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                query = String.format("UPDATE %s SET %s = %s + 1 WHERE %s = %d AND %s = %d", TABLE_INFO,
                        DBFields.INT_CLICKED, DBFields.INT_CLICKED, DBFields.LONG_PROFILE_ID, profileId,
                        DBFields.LONG_ENTRY_ID, entryId);
                conn.createStatement().execute(query);
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (%d, %d, %d, %d, %d)", TABLE_INFO,
                        DBFields.LONG_ENTRY_ID, DBFields.LONG_USER_ID, DBFields.LONG_PROFILE_ID, DBFields.TIME_READ_ON,
                        DBFields.INT_CLICKED, entryId, userId, profileId, CurrentTime.inGMT(), 1);
                conn.createStatement().execute(query);
            }
        } catch (SQLException ex) {
            logger.error("read failed: {}", ex, ex.getMessage());
        }
    }

    public static int removeAllSavedEntries(long userId, long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_SAVED,
                    DBFields.LONG_USER_ID, userId, DBFields.LONG_PROFILE_ID, profileId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("remove all saved entries failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static int removeAllEntriesInfo(long userId, long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d", TABLE_INFO, DBFields.LONG_USER_ID,
                    userId, DBFields.LONG_PROFILE_ID, profileId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            logger.error("remove all entries info failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

}
