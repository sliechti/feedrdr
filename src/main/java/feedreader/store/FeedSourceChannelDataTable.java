package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.XmlChannelData;
import feedreader.utils.SQLUtils;

public class FeedSourceChannelDataTable {

    public static final String TABLE = Constants.FEED_SOURCE_CHANNEL_DATA_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(FeedSourceChannelDataTable.class);
    static XmlChannelData EMPTY = new XmlChannelData();

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static boolean save(long sourceId, XmlChannelData d, boolean force) {
        if (force) {
            try (Connection conn = Database.getConnection()) {
                Database.deleteEntry(conn, TABLE, DBFields.LONG_XML_ID, sourceId);
            } catch (SQLException e) {
                logger.error("save failed: {}", e, e.getMessage());
            }
        }

        return save(sourceId, d);
    }

    public static boolean save(long sourceId, XmlChannelData d) {
        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s', '%s')",
                    TABLE, // Table
                    DBFields.LONG_XML_ID, DBFields.STR_TITLE, DBFields.STR_LINK,
                    DBFields.STR_DESCRIPTION,
                    DBFields.STR_LANG, // Columns
                    sourceId, SQLUtils.asSafeString(d.getTitle()), SQLUtils.asSafeString(d.getLink()),
                    SQLUtils.asSafeString(d.getDescription()), SQLUtils.asSafeString(d.getLanguage())); // values
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate")) {
                return true;
            }
            logger.error("save {}, channel: {}, error: {}", e, sourceId, d, e.getMessage());
        }

        return false;
    }

    public static XmlChannelData get(long id) {
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.getEntry(conn, TABLE, DBFields.LONG_XML_ID, id);
            if (rs.next()) {
                return getNewXmlChannelData(rs);
            }
            return EMPTY;
        } catch (SQLException e) {
            logger.error("get {} failed: {}", e, id, e.getMessage());
            return EMPTY;
        }
    }

    private static XmlChannelData getNewXmlChannelData(ResultSet rs) {
        try {
            XmlChannelData ret = new XmlChannelData();
            ret.process("title", rs.getString(DBFields.STR_TITLE));
            ret.process("link", rs.getString(DBFields.STR_LINK));
            ret.process("language", rs.getString(DBFields.STR_LANG));
            ret.process("description", rs.getString(DBFields.STR_DESCRIPTION));
            return ret;
        } catch (SQLException e) {
            logger.error("get new xml channel data failed: {}", e, e.getMessage());
        }

        return EMPTY;
    }

}
