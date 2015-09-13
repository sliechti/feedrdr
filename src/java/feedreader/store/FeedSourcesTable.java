package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.cron.CronTimeUtils;
import feedreader.entities.FeedSourceEntry;
import feedreader.log.Logger;
import feedreader.time.CurrentTime;
import feedreader.utils.SQLUtils;

/**
 * 
 */
public class FeedSourcesTable {

    public static final String TABLE = Constants.FEED_SOURCES_TABLE;

    static Class<?> clz = FeedSourcesTable.class; // Easier for logging.

    static Connection conn;
    static Statement stmt;

    static final FeedSourceEntry emptyEntry = new FeedSourceEntry(0, "");
    static final EntriesCount emptyEntriesCount = new EntriesCount(0, 0, 0, 0, 0);

    /**
     * 
     */
    public enum RetCodes {
        ERROR, QUEUED, IN_QUEUE
    };

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

    /**
     * Adds a new source to the {@link Constants#FEED_SOURCES_TABLE} table. The field {@link DBFields#TIME_VALIDATED_AT}
     * is per default 0.
     *
     * @param xmlUrl
     *            http URL for the feed to be added
     *
     * @return {@link RetCodes#ERROR} <br>
     *         {@link RetCodes#IN_QUEUE}<br>
     *         {@link RetCodes#QUEUED}
     *
     */
    public static RetCodes addNewSource(String xmlUrl) {
        try {
            xmlUrl = xmlUrl.toLowerCase();
            String query = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', %d)", TABLE, DBFields.STR_XML_URL,
                    DBFields.TIME_ADDED_AT, SQLUtils.asSafeString(xmlUrl), CurrentTime.inGMT());
            Logger.debugSQL(clz).log("addNewSource ").log(query).end();
            stmt.execute(query);
            return RetCodes.QUEUED;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate")) {
                return RetCodes.IN_QUEUE;
            } else {
                Logger.error(clz).log("addNewSource error ").log(ex.getErrorCode()).log(" : ").log(ex.getMessage())
                        .end();
                return RetCodes.ERROR;
            }
        }
    }

    public static void disable(long sourceId) {
        try {
            String query = String.format("UPDATE %s SET %s = true WHERE %s = %d", TABLE, DBFields.BOOL_GAVE_UP,
                    DBFields.LONG_XML_ID, sourceId);
            Logger.debugSQL(clz).log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("disable ").log(sourceId).log(", error ").log(ex.getMessage()).end();
        }
    }

    public static int setError(long id, int code, String err) {
        try {
            String query = String.format("UPDATE %s SET %s=%s +1, %s=%d, %s='%s' WHERE %s=%d RETURNING %s", TABLE,
                    DBFields.SHORT_ERROR_COUNT,
                    DBFields.SHORT_ERROR_COUNT, // +1
                    DBFields.SHORT_ERROR_CODE, code, DBFields.STR_LAST_ERROR, err, DBFields.LONG_XML_ID, id,
                    DBFields.SHORT_ERROR_COUNT);
            ResultSet rs = stmt.executeQuery(query);
            Logger.debugSQL(clz).log(query).end();
            if (rs.next()) {
                return rs.getInt(DBFields.SHORT_ERROR_COUNT);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("setTempError ").log(ex.getMessage()).end();
        }

        return 0;
    }

    public static void updateCheckedTime(FeedSourceEntry entry) {
        try {
            String query = String.format("UPDATE %s set %s = %d WHERE %s = %d", TABLE, DBFields.TIME_CHECKED_AT,
                    CurrentTime.inGMT() + FeedAppConfig.FETCH_SOURCE_WAIT_INTERVAL_IN_SECS, DBFields.LONG_XML_ID,
                    entry.getId());
            Logger.debugSQL(clz).log("updateCheckedTime ").log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("updateCheckedTime id ").log(entry.getId()).log(", error ").log(ex.getMessage())
                    .end();
        }
    }

    public static String updateCount(long xmlId) {
        long max0time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_0_VAL);
        long max1time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_1_VAL);
        long max2time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_2_VAL);

        try {
            String query = String.format("select %s(%d, %d, %d, %d) as %s", Database.Functions.UPDATE_SOURCE_COUNT,
                    xmlId, max0time, max1time, max2time, DBFields.COUNT);

            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(DBFields.COUNT);
            }

            return "nothing 0 0 0";
        } catch (SQLException ex) {
            Logger.error(clz).log("updateCount ").log(xmlId).log(", error ").log(ex.getMessage()).end();

            return ex.getMessage();
        }
    }

    public static boolean setValid(long id) {
        try {
            stmt.execute(String.format("UPDATE %s SET %s=%d WHERE %s = %d", TABLE, DBFields.TIME_VALIDATED_AT,
                    CurrentTime.inGMT(), DBFields.LONG_XML_ID, id));

            return true;
        } catch (SQLException ex) {
            Logger.error(clz).log("setValid ").log(id).log(" ").log(ex.getMessage()).end();
        }

        return false;
    }

    public static void clearErrors(long id) {
        try {
            String query = String.format("UPDATE %s SET %s = 0, %s = 0, %s = '' WHERE %s = %d", TABLE,
                    DBFields.SHORT_ERROR_CODE, DBFields.SHORT_ERROR_COUNT, DBFields.STR_LAST_ERROR,
                    DBFields.LONG_XML_ID, id);
            Logger.debugSQL(clz).log("clear errors ").log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("clear errors ").log(id).log(", error ").log(ex.getMessage()).end();
        }
    }

    public static long getInvalidCount() {
        try {
            String query = String.format("SELECT count(%s) FROM %s where %s=%d", DBFields.LONG_XML_ID, TABLE,
                    DBFields.TIME_VALIDATED_AT, 0);
            Logger.debugSQL(clz).log("getInvalidCount ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("getInvalidCount error ").log(ex.getMessage()).end();
        }

        return -1;
    }

    private static FeedSourceEntry getNewEntry(ResultSet rs) throws SQLException {
        FeedSourceEntry entry = new FeedSourceEntry(rs.getLong(DBFields.LONG_XML_ID),
                rs.getString(DBFields.STR_XML_URL));
        entry.setCheckedAt(rs.getLong(DBFields.TIME_CHECKED_AT));
        entry.setAddedAt(rs.getLong(DBFields.TIME_ADDED_AT));
        return entry;
    }

    /**
     * Gets the next {@link FeedSourceEntry} to be scanned for articles. Where {@link DBFields#TIME_VALIDATED_AT} > 0
     * AND {@link DBFields#BOOL_GAVE_UP} = false. Ordered by {@link DBFields#TIME_CHECKED_AT} and
     * {@link DBFields#TIME_ADDED_AT}, ASC.
     * 
     * @param blockForMs
     * 
     * @return
     */
    public static FeedSourceEntry getNextFetch(int blockForMs) {
        try {
            String query = String.format("SELECT * FROM  %s WHERE %s > %d AND %s = %b ORDER BY %s, %s ASC LIMIT 1",
                    TABLE, DBFields.TIME_VALIDATED_AT, 0, DBFields.BOOL_GAVE_UP, false, DBFields.TIME_CHECKED_AT,
                    DBFields.TIME_ADDED_AT);

            Logger.debugSQL(clz).log("getNext block ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                return emptyEntry;
            }

            FeedSourceEntry entry = getNewEntry(rs);

            long id = entry.getId();

            query = String.format("UPDATE %s SET %s=%d WHERE %s='%s'", TABLE, DBFields.TIME_CHECKED_AT,
                    CurrentTime.inGMT(), DBFields.LONG_XML_ID, id);
            stmt.execute(query);
            Logger.debugSQL(clz).log("getNext block update ").log(query).end();

            return entry;
        } catch (SQLException ex) {
            Logger.error(clz).log("getNext error ").log(ex.getMessage()).end();
        }

        return emptyEntry;
    }

    public static FeedSourceEntry getByField(String fieldName, long val) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE, fieldName, val);
        return runQuery(query);
    }

    public static FeedSourceEntry getByField(String fieldName, String val) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE, fieldName, val);
        return runQuery(query);
    }

    public static FeedSourceEntry runQuery(String query) {
        try {
            Logger.debugSQL(clz).log("runQuery ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                return emptyEntry;
            }

            return getNewEntry(rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("getByStrKey error ").log(ex.getMessage()).end();
        }

        return emptyEntry;
    }

    public static ResultSet getEntries() {
        try {
            String query = String.format("SELECT * FROM %s", TABLE);
            return stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("getEntries error ").log(ex.getMessage()).end();
        }

        return null;
    }

    /**
     * Returns an entry in the {@link FeedSourcesTable#TABLE} which hasn't been validated yet. Where
     * {@link DBFields#TIME_VALIDATED_AT} = 0 and {@link DBFields#BOOL_GAVE_UP} = false.
     * 
     * @param blockForMs
     * @return {@link FeedSourceEntry}
     */
    public static FeedSourceEntry getNextInvalid(int blockForMs) {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s=%d AND %s=%b ORDER BY %s ASC, %s ASC LIMIT 1",
                    TABLE, DBFields.TIME_VALIDATED_AT, 0, DBFields.BOOL_GAVE_UP, false, DBFields.TIME_CHECKED_AT,
                    DBFields.TIME_ADDED_AT);
            Logger.debugSQL(clz).log("getNextInvalid block ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                long id = rs.getLong(DBFields.LONG_XML_ID);
                String xmlUrl = rs.getString(DBFields.STR_XML_URL);
                FeedSourceEntry entry = new FeedSourceEntry(id, xmlUrl);

                query = String.format("UPDATE %s SET %s=%d WHERE %s=%d", TABLE, DBFields.TIME_CHECKED_AT,
                        CurrentTime.inGMT(), DBFields.LONG_XML_ID, id);
                stmt.execute(query);
                Logger.debugSQL(clz).log("getNextInvalid block ").log(query).end();

                return entry;
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("getNextInvalid error ").log(ex.getMessage()).end();
        }

        return emptyEntry;
    }

    public static boolean isKnown(String xmlUrl) {
        // try
        // {
        //
        // }
        // catch (SQLException ex)
        // {
        // Logger.error(clz).log("error executing statement ").log(ex.getMessage()).end();
        // }

        return false;
    }

    public static class EntriesCount {

        public long totalEntries;
        public long count0;
        public long count1;
        public long count2;
        public long totalSince;

        public EntriesCount(long totalEntries, long count0, long count1, long count2, long totalSince) {
            this.totalEntries = totalEntries;
            this.count0 = count0;
            this.count1 = count1;
            this.count2 = count2;
            this.totalSince = totalSince;
        }

        @Override
        public String toString() {
            return "EntriesCount{" + "totalEntries=" + totalEntries + ", count0=" + count0 + ", " + "count1=" + count1
                    + ", count2=" + count2 + ", totalSince=" + totalSince + '}';
        }
    }

    public static EntriesCount getTotalEntriesCount(String xmlIds, long timestamp) {
        try {
            String query = "select f1 from feedreader.gettotalentriescountsince('" + xmlIds + "', " + timestamp + ");";
            ResultSet rs = Database.getStatement().executeQuery(query);
            long totalSince = -1;
            if (rs.next())
                totalSince = rs.getLong(1);

            query = "select * from feedreader.gettotalentriescount('" + xmlIds + "')";
            rs = Database.getStatement().executeQuery(query);
            if (rs.next()) {
                if (totalSince == -1)
                    rs.getLong(1);
                return new EntriesCount(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getLong(4), totalSince);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("getTotalEntriesCount ").log(xmlIds).log(", error ").log(ex.getMessage()).end();
        }

        return emptyEntriesCount;
    }

}
