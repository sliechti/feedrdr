package feedreader.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.XmlChannelImage;
import feedreader.utils.SQLUtils;

public class FeedSourceChannelImageTable {

    public static final String TABLE = Constants.FEED_SOURCE_IMAGE_DATA_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(FeedSourceChannelImageTable.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static boolean save(long sourceId, XmlChannelImage d, boolean force) {
        if (force) {
            try (Connection conn = Database.getConnection()) {
                Database.deleteEntry(conn, TABLE, DBFields.LONG_XML_ID, sourceId);
            } catch (SQLException e) {
                logger.error("save failed {}, channel {}, force {}, error {}", e, sourceId, d, force);
            }
        }

        return save(sourceId, d);
    }

    public static boolean save(long sourceId, XmlChannelImage d) {
        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s', '%s', %d, %d)",
                    TABLE, // Table
                    DBFields.LONG_XML_ID, DBFields.STR_TITLE, DBFields.STR_IMG_URL, DBFields.STR_LINK,
                    DBFields.STR_DESCRIPTION,
                    DBFields.INT_WIDTH,
                    DBFields.INT_HEIGHT, // Columns
                    sourceId, SQLUtils.asSafeString(d.getTitle()), SQLUtils.asSafeString(d.getUrl()),
                    SQLUtils.asSafeString(d.getLink()), SQLUtils.asSafeString(d.getDescription()), d.getWidth(),
                    d.getHeight()); // values
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate")) {
                return true;
            }
            logger.error("save {}, channel {}, failed {}", e, sourceId, d, e.getMessage());
        }

        return false;
    }
}
