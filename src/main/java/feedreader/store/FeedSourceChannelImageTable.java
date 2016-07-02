package feedreader.store;

import feedreader.config.Constants;
import feedreader.entities.XmlChannelImage;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedSourceChannelImageTable {

    public static final String TABLE = Constants.FEED_SOURCE_IMAGE_DATA_TABLE;

    static Class<?> clz = FeedSourceChannelImageTable.class; // Easier for logging.
    static Connection conn;

    public static boolean init() {
        conn = Database.getConnection();
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

    public static boolean save(long sourceId, XmlChannelImage d, boolean force) {
        if (force) {
            try {
                Database.deleteEntry(TABLE, DBFields.LONG_XML_ID, sourceId);
            } catch (SQLException ex) {
                Logger.error(clz).log("save forced ").log(ex.getMessage()).end();
            }
        }

        return save(sourceId, d);
    }

    public static boolean save(long sourceId, XmlChannelImage d) {
        try {
            Statement stmt = conn.createStatement();

            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s', '%s', %d, %d)",
                    TABLE, // Table
                    DBFields.LONG_XML_ID, DBFields.STR_TITLE, DBFields.STR_IMG_URL, DBFields.STR_LINK,
                    DBFields.STR_DESCRIPTION,
                    DBFields.INT_WIDTH,
                    DBFields.INT_HEIGHT,// Columns
                    sourceId, SQLUtils.asSafeString(d.getTitle()), SQLUtils.asSafeString(d.getUrl()),
                    SQLUtils.asSafeString(d.getLink()), SQLUtils.asSafeString(d.getDescription()), d.getWidth(),
                    d.getHeight()); // values

            Logger.debugSQL(clz).log(query).end();

            stmt.execute(query);
            return true;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate"))
                return true;
            Logger.error(clz).log("save error, id ").log(sourceId).log("/").log(ex.getMessage()).end();
        }

        return false;
    }
}
