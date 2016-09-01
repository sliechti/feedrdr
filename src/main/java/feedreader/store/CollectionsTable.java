package feedreader.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.utils.SQLUtils;

public class CollectionsTable {

    public static final String TABLE = Constants.SOURCE_COLLECTIONS;
    public static final String TABLE_LIST = Constants.SOURCE_COLLECTIONS_LIST;
    public static final String TABLE_SUBS = Constants.COLLECTIONS;
    private static final Logger logger = LoggerFactory.getLogger(CollectionsTable.class);
    static Connection conn;
    static Statement stmt;

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static String getQueryCollectionEntriesByCollectionIds(String ids) {
        return "SELECT t1.l_collection_id, "
                + "t1.s_feed_name, "
                + "t2.l_xml_id, "
                + "t2.s_xml_url, "
                + "t3.s_title, "
                + "t3.s_link "
                + "FROM feedreader.sourcecollectionslist AS t1 "
                + "LEFT JOIN feedreader.feedsources AS t2 ON t1.l_xml_id = t2.l_xml_id "
                + "LEFT JOIN feedreader.feedsourcechanneldata AS t3 "
                + "ON t2.l_xml_id = t3.l_xml_id "
                + "WHERE t1.l_collection_id IN (" + SQLUtils.asSafeString(ids) + ") AND t3.s_link != ''";
    }

    public static String getQueryList(long userId) {
        return "SELECT t0.*, count(t1.l_xml_id) as i_feeds FROM feedreader.sourcecollections as t0 " +
                "LEFT JOIN feedreader.sourcecollectionslist AS t1 " +
                "ON t0.l_collection_id = t1.l_collection_id where t0.l_collection_id NOT IN"+ 
                "(SELECT DISTINCT(l_collection_id) from feedreader.collections where l_user_id="+userId +") "  +
                "GROUP BY t0.l_collection_id;";
    }
    
	/**
	 * query to insert subscribed collection Id and userId into collections
	 * table.
	 * 
	 * @return int
	 */
	public static int save(long userId, long collectionId) {
		try (Connection conn = Database.getConnection()) {
			String query = String.format(
					"INSERT INTO feedreader.collections (l_user_id, l_collection_id) VALUES  "
							+ "(%d, %d)",
					// values
					userId, collectionId);

			conn.createStatement().execute(query);
			return 1;
		} catch (SQLException e) {
			logger.error("error occur: {}", e, e.getMessage());
		}
		return -1;
    }

}
