package feedreader.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import feedreader.config.Constants;
import feedreader.log.Logger;

public class CollectionsTable {

    public static final String TABLE = Constants.SOURCE_COLLECTIONS;
    public static final String TABLE_LIST = Constants.SOURCE_COLLECTIONS_LIST;

    static Class<?> clz = CollectionsTable.class; // Easier for logging.

    static Connection conn;
    static Statement stmt;

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

    public static String getQueryCollectionEntriesByCollectionIds(String ids) {
        return "SELECT t1.l_collection_id, t1.s_feed_name, t2.l_xml_id, t2.s_xml_url, t3.s_title, t3.s_link " +
                "FROM feedreader.sourcecollectionslist AS t1 " +
                "LEFT JOIN feedreader.feedsources as t2 " +
                "ON t1.l_xml_id = t2.l_xml_id " +
                "LEFT JOIN feedreader.feedsourcechanneldata as t3 " +
                "ON t2.l_xml_id = t3.l_xml_id WHERE t1.l_collection_id IN (" + ids + ")";
    }

    public static String getQueryList() {
        return "SELECT t0.*, count(t1.l_xml_id) as i_feeds FROM feedreader.sourcecollections as t0 " +
                "LEFT JOIN feedreader.sourcecollectionslist AS t1 " +
                "ON t0.l_collection_id = t1.l_collection_id " +
                "GROUP BY t0.l_collection_id;";
    }
    
}

