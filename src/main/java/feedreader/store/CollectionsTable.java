package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.OPMLEntry;
import feedreader.utils.SQLUtils;

public class CollectionsTable {

    public static final String TABLE = Constants.SOURCE_COLLECTIONS;

    public static final String TABLE_LIST = Constants.SOURCE_COLLECTIONS_LIST;
    private static final Logger logger = LoggerFactory.getLogger(CollectionsTable.class);

    public static void close() {
        logger.info("close");
    }

    public static SourceCollection getCollection(long id) throws SQLException {
        ResultSet rs = Database.getEntry(Database.getConnection(),
                "feedreader.sourcecollections",
                DBFields.LONG_COLLECTION_ID,
                id);
        if (rs.next()) {
            return SourceCollection.fromRs(rs);
        }
        return SourceCollection.NULL;
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
    public static String getQueryList() {
        return "SELECT t0.*, count(t1.l_xml_id) as i_feeds FROM feedreader.sourcecollections as t0 " +
                "LEFT JOIN feedreader.sourcecollectionslist AS t1 " +
                "ON t0.l_collection_id = t1.l_collection_id " +
                "GROUP BY t0.l_collection_id;";
    }

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static SourceCollection addCollection(long userId, long collId, List<Long> profiles) throws Exception {

        SourceCollection collection = getCollection(collId);
        if (collection.isNull()) {
            throw new Exception("collection not found");
        }

        List<Long> validProfiles = UserProfilesTable.validate(userId, profiles);
        if (validProfiles.isEmpty()) {
            throw new Exception("At least one profile is required");
        }

        long streamId = UserStreamGroupsTable.save(userId, collection.getName());
        if (streamId == 0) {
            throw new Exception("couldn't create stream group '" + collection.getName() + "'");
        }

        try (Connection conn = Database.getConnection()) {
            String query = CollectionsTable.getQueryCollectionEntriesByCollectionIds("" + collection.getId());
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String feedName = rs.getString(DBFields.STR_FEED_NAME);
                OPMLEntry entry = new OPMLEntry(feedName, rs.getString(DBFields.STR_XML_URL));
                long xmlId = rs.getLong(DBFields.LONG_XML_ID);

                long subsId = UserFeedSubscriptionsTable.save(userId, xmlId, entry);

                if (subsId == -1) {
                    logger.error("error addin subscription {}, entry {}", xmlId, entry);
                    continue;
                }

                UserStreamGroupsTable.addSubscriptionToStream(streamId, subsId);

                for (Long id : validProfiles) {
                    UserProfilesTable.addStreamToProfile(streamId, id);
                }
            }
        } catch (SQLException e) {
            logger.error("add collection failed: {}", e, e.getMessage());
            throw new Exception("sql error: " + e.getMessage());
        }
        return collection;
    }

    public static class SourceCollection {
        public static final SourceCollection NULL = new SourceCollection(0, "", "");
        private final String description;
        private final long id;
        private final String name;

        public SourceCollection(long id, String name, String description) {
            this.name = name;
            this.id = id;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isNull() {
            return this == NULL;
        }

        public static SourceCollection fromRs(ResultSet rs) throws SQLException {
            return new SourceCollection(rs.getLong(DBFields.LONG_COLLECTION_ID),
                    rs.getString(DBFields.STR_NAME),
                    rs.getString(DBFields.STR_DESCRIPTION));
        }

    }

}
