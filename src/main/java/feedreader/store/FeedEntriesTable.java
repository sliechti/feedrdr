package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.parser.XmlFeedEntry;
import feedreader.time.CurrentTime;
import feedreader.utils.SQLUtils;

public class FeedEntriesTable {

    private static final Logger logger = LoggerFactory.getLogger(FeedEntriesTable.class);
    public static final String TABLE = Constants.FEED_ENTRIES_TABLE;
    public static final String TABLE_DATA = Constants.FEED_ENTRIES_DATA_TABLE;

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static int save(long sourceId, XmlFeedEntry news, boolean deleteFirst) {
        if (deleteFirst) {
            try (Connection conn = Database.getConnection()) {
                String query = String.format("DELETE FROM %s WHERE %s = '%s' or %s = '%s'", TABLE, DBFields.STR_LINK,
                        news.getLink(), DBFields.STR_TITLE, SQLUtils.asSafeString(news.getTitle()));
                conn.createStatement().execute(query);
            } catch (Exception e) {
                logger.error("save {}, news {}, delete {} failed {}", e, sourceId, news, deleteFirst, e.getMessage());
            }
        }

        return save(sourceId, news);
    }

    public static int save(long sourceId, XmlFeedEntry news) {
        try (Connection conn = Database.getConnection()) {
            // Fails if duplicate key.
            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES  "
                            + "(%d, '%s', '%s', '%s', '%s', '%s', %d, %d, '%s')",
                    TABLE,
                    // Fields
                    DBFields.LONG_XML_ID, DBFields.STR_LINK, DBFields.STR_TITLE, DBFields.STR_DESCRIPTION,
                    DBFields.STR_CONTENT, DBFields.STR_CLEAN_CONTENT,
                    DBFields.TIME_PUBLICATION_DATE,
                    DBFields.TIME_DISCOVERED_AT,
                    DBFields.STR_THUMB_URL,
                    // Values
                    sourceId, SQLUtils.asSafeString(news.getLink()), SQLUtils.asSafeString(news.getTitle()),
                    SQLUtils.asSafeString(news.getDescription()), SQLUtils.asSafeString(news.getContent()),
                    SQLUtils.asSafeString(news.getCleanContent()), news.getEntryDate(), CurrentTime.inGMT(),
                    SQLUtils.asSafeString(news.getThumbImg()));

            conn.createStatement().execute(query);
            return 1;
        } catch (SQLException e) {
            // TODO: check for publication date. Update.
            if (e.getMessage().contains("duplicate")) {
                return 0;
            }
            logger.error("save error: {}", e, e.getMessage());
            return -1;
        }
    }

    public static ResultSet getEntries(long sourceId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM %s WHERE %s=%d ORDER BY %s DESC", TABLE, DBFields.LONG_XML_ID,
                    sourceId, DBFields.TIME_PUBLICATION_DATE);
            // Bad place to debug.
            return conn.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            logger.error("getEntries sourceId {}, error: {}", ex, sourceId, ex.getMessage());
        }

        return null;
    }

    public static int clicked(long entryId) {
        // try
        // {
        // String query = String.format("UPDATE %s SET %s = %s + 1 WHERE %s = %d",
        // TABLE_DATA, DBFields.LONG_CLICKS, DBFields.LONG_CLICKS,
        // DBFields.LONG_ENTRY_ID, entryId);
        // Logger.error(clz).log(query).end();
        // if (stmt.executeUpdate(query) == 0)
        // {
        // String query = String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d)",
        // TABLE_DATA, DBFields.LONG_CLICKS, DBFields.LONG_CLICKS,
        // DBFields.LONG_ENTRY_ID, entryId);
        // Logger.error(clz).log(query).end();
        // }
        // }
        // catch (SQLException ex)
        // {
        // Logger.error(clz).log("clicked ").log(entryId).log(", error ").log(ex.getMessage()).end();
        // }

        return 0;
    }

    /**
     * Get a list of all article entries for the requested source where discoveredAt greater than discoveredFrom.
     *
     * @param sourceUrl
     * @param discoveredFrom
     * @param opts
     * @return
     */
    // public static List<FeedEntry> getFeedEntries(String sourceUrl, long discoveredFrom, MongoQueryOptions opts)
    // {
    // BasicDBObject q = new BasicDBObject(FeedTags.TAG_XML_URL, sourceUrl);
    //
    // if (discoveredFrom > 0) {
    // q.append(FeedTags.TAG_DISCOVERED_AT, new BasicDBObject(MongoConstants.GT, discoveredFrom));
    // }
    //
    // DBCursor c = coll.find(q);
    //
    // if (opts.hasSort()) {
    // c.sort(new BasicDBObject(opts.getSortTag(), opts.getSort()));
    // }
    //
    // if (opts.hasLimit()) {
    // c.limit(opts.getLimit());
    // }
    //
    // List<FeedEntry> ret = new ArrayList(c.size());
    // while (c.hasNext()) {
    // FeedEntry entry = new FeedEntry((BasicDBObject)c.next());
    // ret.add(entry);
    // }
    //
    // return ret;
    // throw new RuntimeException(EMPTY);
    // }
}
