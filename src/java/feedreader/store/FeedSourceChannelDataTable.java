package feedreader.store;

import feedreader.config.Constants;
import feedreader.entities.XmlChannelData;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedSourceChannelDataTable {

    public static final String TABLE = Constants.FEED_SOURCE_CHANNEL_DATA_TABLE;

    static Class<?> clz = FeedSourceChannelDataTable.class; // Easier for logging.
    static Connection conn;

    static XmlChannelData EMPTY = new XmlChannelData();

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

    public static boolean save(long sourceId, XmlChannelData d, boolean force) {
        if (force) {
            try {
                Database.deleteEntry(TABLE, DBFields.LONG_XML_ID, sourceId);
            } catch (SQLException ex) {
                Logger.error(clz).log("save forced ").log(ex.getMessage()).end();
            }
        }

        return save(sourceId, d);
    }

    public static boolean save(long sourceId, XmlChannelData d) {
        try {
            Statement stmt = conn.createStatement();

            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s', '%s')",
                    TABLE, // Table
                    DBFields.LONG_XML_ID, DBFields.STR_TITLE, DBFields.STR_LINK,
                    DBFields.STR_DESCRIPTION,
                    DBFields.STR_LANG, // Columns
                    sourceId, SQLUtils.asSafeString(d.getTitle()), SQLUtils.asSafeString(d.getLink()),
                    SQLUtils.asSafeString(d.getDescription()), SQLUtils.asSafeString(d.getLanguage())); // values

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

    public static XmlChannelData get(long id) {
        try {
            ResultSet rs = Database.getEntry(TABLE, DBFields.LONG_XML_ID, id);
            if (rs.next()) {
                return getNewXmlChannelData(rs);
            }

            return EMPTY;
        } catch (SQLException ex) {
            Logger.error(clz).log("get error, ").log(ex.getMessage()).end();
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
        } catch (SQLException ex) {
            Logger.error(clz).log("getNewXmlChannelData error, ").log(ex.getMessage()).end();
        }

        return EMPTY;
    }

}
