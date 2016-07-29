package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.cron.CronTimeUtils;
import feedreader.entities.FeedSourceEntry;
import feedreader.time.CurrentTime;
import feedreader.utils.SQLUtils;

public class FeedSourcesTable {

    private static final Logger logger = LoggerFactory.getLogger(FeedSourcesTable.class);
    public static final String TABLE = Constants.FEED_SOURCES_TABLE;
    static final FeedSourceEntry emptyEntry = new FeedSourceEntry(0, "");
    static final EntriesCount emptyEntriesCount = new EntriesCount(0, 0, 0, 0, 0);

    /**
     *
     */
    public enum RetCodes {
        ERROR, QUEUED, IN_QUEUE
    }

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    /**
     * Adds a new source to the {@link Constants#FEED_SOURCES_TABLE} table. The field {@link DBFields#TIME_VALIDATED_AT}
     * is per default 0.
     *
     * @param xmlUrl
     * http URL for the feed to be added
     *
     * @return {@link RetCodes#ERROR} <br>
     * {@link RetCodes#IN_QUEUE}<br>
     * {@link RetCodes#QUEUED}
     *
     */
    public static RetCodes addNewSource(String xmlUrl) {
        try (Connection conn = Database.getConnection()) {
            xmlUrl = xmlUrl.toLowerCase();
            String query = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', %d)", TABLE, DBFields.STR_XML_URL,
                    DBFields.TIME_ADDED_AT, SQLUtils.asSafeString(xmlUrl), CurrentTime.inGMT());
            conn.createStatement().execute(query);
            return RetCodes.QUEUED;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate")) {
                return RetCodes.IN_QUEUE;
            }
            logger.error("add new source error: {}", ex, ex.getMessage());
            return RetCodes.ERROR;
        }
    }

    public static void disable(long sourceId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = true WHERE %s = %d", TABLE, DBFields.BOOL_GAVE_UP,
                    DBFields.LONG_XML_ID, sourceId);
            conn.createStatement().execute(query);
        } catch (SQLException e) {
            logger.error("disable {}, failed: {}", e, sourceId, e.getMessage());
        }
    }

    public static int setError(long id, int code, String err) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s=%s +1, %s=%d, %s='%s' WHERE %s=%d RETURNING %s", TABLE,
                    DBFields.SHORT_ERROR_COUNT,
                    DBFields.SHORT_ERROR_COUNT, // +1
                    DBFields.SHORT_ERROR_CODE, code, DBFields.STR_LAST_ERROR, err, DBFields.LONG_XML_ID, id,
                    DBFields.SHORT_ERROR_COUNT);
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getInt(DBFields.SHORT_ERROR_COUNT);
            }
        } catch (SQLException e) {
            logger.error("set error failed: {}", e, e.getMessage());
        }

        return 0;
    }

    public static void updateCheckedTime(FeedSourceEntry entry) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s set %s = %d WHERE %s = %d", TABLE, DBFields.TIME_CHECKED_AT,
                    CurrentTime.inGMT() + FeedAppConfig.FETCH_SOURCE_WAIT_INTERVAL_IN_SECS, DBFields.LONG_XML_ID,
                    entry.getId());
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            logger.error("update checked time for entry {}, error: {}", ex, entry, ex.getMessage());
        }
    }

    public static String updateCount(long xmlId) {
        long max0time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_0_VAL);
        long max1time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_1_VAL);
        long max2time = CronTimeUtils.getMaxHistory(FeedAppConfig.USER_2_VAL);

        try (Connection conn = Database.getConnection()) {
            String query = String.format("select %s(%d, %d, %d, %d) as %s", Database.Functions.UPDATE_SOURCE_COUNT,
                    xmlId, max0time, max1time, max2time, DBFields.COUNT);

            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getString(DBFields.COUNT);
            }

            return "nothing 0 0 0";
        } catch (SQLException ex) {
            logger.error("update count {}, error: {}", ex, xmlId, ex.getMessage());
            return ex.getMessage();
        }
    }

    public static boolean setValid(long id) {
        try (Connection conn = Database.getConnection()) {
            conn.createStatement()
                    .execute(String.format("UPDATE %s SET %s=%d WHERE %s = %d", TABLE, DBFields.TIME_VALIDATED_AT,
                            CurrentTime.inGMT(), DBFields.LONG_XML_ID, id));
            return true;
        } catch (SQLException ex) {
            logger.error("set valid: {}, error: {}", ex, id, ex.getMessage());
        }

        return false;
    }

    public static void clearErrors(long id) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = 0, %s = 0, %s = '' WHERE %s = %d", TABLE,
                    DBFields.SHORT_ERROR_CODE, DBFields.SHORT_ERROR_COUNT, DBFields.STR_LAST_ERROR,
                    DBFields.LONG_XML_ID, id);
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            logger.error("clear errors id {}, error: {}", ex, id, ex.getMessage());
        }
    }

    public static long getInvalidCount() {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT count(%s) FROM %s where %s=%d", DBFields.LONG_XML_ID, TABLE,
                    DBFields.TIME_VALIDATED_AT, 0);
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            logger.error("get invalid count error: {}", ex, ex.getMessage());
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
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM  %s WHERE %s > %d AND %s = %b ORDER BY %s, %s ASC LIMIT 1",
                    TABLE, DBFields.TIME_VALIDATED_AT, 0, DBFields.BOOL_GAVE_UP, false, DBFields.TIME_CHECKED_AT,
                    DBFields.TIME_ADDED_AT);

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (!rs.next()) {
                return emptyEntry;
            }

            FeedSourceEntry entry = getNewEntry(rs);

            long id = entry.getId();

            query = String.format("UPDATE %s SET %s=%d WHERE %s='%s'", TABLE, DBFields.TIME_CHECKED_AT,
                    CurrentTime.inGMT(), DBFields.LONG_XML_ID, id);
            conn.createStatement().execute(query);
            return entry;
        } catch (SQLException ex) {
            logger.error("get next error: {}", ex, ex.getMessage());
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
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (!rs.next()) {
                return emptyEntry;
            }

            return getNewEntry(rs);
        } catch (SQLException ex) {
            logger.error("get by key str error: {}", ex, ex.getMessage());
        }

        return emptyEntry;
    }

    public static ResultSet getEntries() {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM %s", TABLE);
            return conn.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            logger.error("get entries error: {}", ex, ex.getMessage());
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
        try (Connection conn = Database.getConnection()){
            String query = String.format("SELECT * FROM %s WHERE %s=%d AND %s=%b ORDER BY %s ASC, %s ASC LIMIT 1",
                    TABLE, DBFields.TIME_VALIDATED_AT, 0, DBFields.BOOL_GAVE_UP, false, DBFields.TIME_CHECKED_AT,
                    DBFields.TIME_ADDED_AT);
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                long id = rs.getLong(DBFields.LONG_XML_ID);
                String xmlUrl = rs.getString(DBFields.STR_XML_URL);
                FeedSourceEntry entry = new FeedSourceEntry(id, xmlUrl);

                query = String.format("UPDATE %s SET %s=%d WHERE %s=%d", TABLE, DBFields.TIME_CHECKED_AT,
                        CurrentTime.inGMT(), DBFields.LONG_XML_ID, id);
                conn.createStatement().execute(query);
                return entry;
            }
        } catch (SQLException ex) {
            logger.error("get next invalid error: {}", ex, ex.getMessage());
        }

        return emptyEntry;
    }

    /**
     * @param xmlUrl
     */
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
        try (Connection conn = Database.getConnection()){
            String query = "select f1 from feedreader.gettotalentriescountsince('" + xmlIds + "', " + timestamp + ");";
            ResultSet rs = conn.createStatement().executeQuery(query);
            long totalSince = -1;
            if (rs.next())
                totalSince = rs.getLong(1);

            query = "select * from feedreader.gettotalentriescount('" + xmlIds + "')";
            rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                if (totalSince == -1)
                    rs.getLong(1);
                return new EntriesCount(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getLong(4), totalSince);
            }
        } catch (SQLException ex) {
            logger.error("get total entries count: {}, error {}", ex, xmlIds, ex.getMessage());
        }

        return emptyEntriesCount;
    }

}
