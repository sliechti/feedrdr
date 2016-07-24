package feedreader.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinedTable {

    private static final Logger logger = LoggerFactory.getLogger(JoinedTable.class);
    private static final String allXmlIdsQuery = "select t2.l_xml_id from feedreader.userstreamgroups as t0 "
            + " right join feedreader.userstreamgroupfeedsubscription as t1"
            + "  on t0.l_stream_id = t1.l_stream_id"
            + " inner join feedreader.userfeedsubscriptions as t2" + "  on t2.l_subs_id = t1.l_subs_id"
            + " where t0.l_stream_id = ?  and t0.l_user_id = ?";

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static long allStreamUnreadEntries(long streamId, long userId, long userMaxHistTime, StringBuilder entries) {
        long start = System.currentTimeMillis();
        long unreadEntriesCount = 0;

        try (Connection conn = Database.getConnection()) {
            ResultSet rs = getAllXmlIdsWithReadMarkers(conn, streamId, userId);
            while (rs.next()) {
                long feedMaxTime = rs.getLong(2);
                long timelineMax = 0;
                if (feedMaxTime > userMaxHistTime) {
                    timelineMax = feedMaxTime;
                } else {
                    timelineMax = userMaxHistTime;
                }

                // xmlIds.append(rs.getString(1)).append(",");
                String query = "select t0.l_entry_id from feedreader.feedentries as t0 \n"
                        + "    left join feedreader.userfeedentriesinfo as t1\n"
                        + "        on t0.l_entry_id = t1.l_entry_id\n" + "where t0.l_xml_id = " + rs.getLong(1)
                        + " and t_pub_date > " + timelineMax + "\n" + "and (t1.b_read = false or t1.b_read is null)";
                // INFO: POssible to limit query. In that case we do need to sort entries here instead
                // of in the next big query.
                ResultSet sub = conn.createStatement().executeQuery(query);

                while (sub.next()) {
                    entries.append(sub.getString(1)).append(",");
                    unreadEntriesCount++;
                }
            }
            if (entries.length() > 0) {
                entries.setLength(entries.length() - 1);
            }
        } catch (SQLException ex) {
            logger.error("all stream unread, stream id {}, user id {}, error: {}", ex, streamId, userId,
                    ex.getMessage());
            return -1;
        }

        if (System.currentTimeMillis() - start > 100) {
            logger.error("unread entries for stream id {} took longer than 100ms", streamId);
        }

        return unreadEntriesCount;
    }

    public static String getAllReadEntries(long profileId, long maxTime, long maxRet, String xmlIds) {
        String query = "SELECT feedreader.getreadentries(" + profileId + ", " + maxTime + "," + maxRet + ",'" + xmlIds
                + "')";
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            }
            return "";

        } catch (SQLException ex) {
            logger.error("query error: {}, {}", ex, query, ex.getMessage());
        }

        return "";
    }

    public static ResultSet getAllXmlIdsWithReadMarkers(Connection conn, long streamId, long userId) {
        try {
            String query = "select t2.l_xml_id, t2.t_read_marker from feedreader.userstreamgroups as t0  \n"
                    + "    inner join feedreader.userstreamgroupfeedsubscription as t1  \n"
                    + "        on t0.l_stream_id = t1.l_stream_id \n"
                    + "    inner join feedreader.userfeedsubscriptions as t2  \n"
                    + "        on t2.l_subs_id = t1.l_subs_id where \n" + "\n" + "t0.l_stream_id = " + streamId
                    + "  and t0.l_user_id = " + userId;

            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            logger.error("get all xmlids failed: {}", e, e.getMessage());
        }

        return null;
    }

    public static String getAllXmlIds(long streamId, long userId) {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(allXmlIdsQuery);
            stmt.setLong(1, streamId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next())
                sb.append(rs.getString(1)).append(",");
            if (sb.length() > 0)
                sb.setLength(sb.length() - 1);

            return sb.toString();
        } catch (SQLException ex) {
            logger.error("get all xml ids for stream id {}, user id {}", ex, streamId, userId);
        }

        return "";
    }
}
