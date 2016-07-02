package feedreader.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import feedreader.log.Logger;

public class JoinedTable {

    static Class<?> clz = JoinedTable.class; // Easier for logging.

    static Connection conn;
    static Statement stmt;

    static PreparedStatement allXmlIdsFromStreamGroup, allReadEntriesForUserProfile;

    public static boolean init() {
        conn = Database.getConnection();
        stmt = Database.getStatement();

        try {
            allXmlIdsFromStreamGroup = Database.getConnection().prepareStatement(
                    "select t2.l_xml_id from feedreader.userstreamgroups as t0 "
                            + " right join feedreader.userstreamgroupfeedsubscription as t1"
                            + "  on t0.l_stream_id = t1.l_stream_id"
                            + " inner join feedreader.userfeedsubscriptions as t2" + "  on t2.l_subs_id = t1.l_subs_id"
                            + " where t0.l_stream_id = ?  and t0.l_user_id = ?");
        } catch (SQLException ex) {
            Logger.error(clz).log("init ").log(ex.getMessage()).end();
        }

        Logger.info(clz).log("initialized.").end();
        return true;
    }

    public static void close() {
        Logger.info(clz).log("close()").end();

        try {
            allXmlIdsFromStreamGroup.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.error(clz).log("closing sql objects ").log(ex.getMessage()).end();
        }
    }

    public static long allStreamUnreadEntries(long streamId, long userId, long userMaxHistTime, StringBuilder entries) {
        ResultSet rs = getAllXmlIdsWithReadMarkers(streamId, userId);
        Statement s = Database.getStatement();
        long start = System.currentTimeMillis();
        long unreadEntriesCount = 0;
        try {
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
                ResultSet sub = s.executeQuery(query);

                while (sub.next()) {
                    entries.append(sub.getString(1)).append(",");
                    unreadEntriesCount++;
                }
                ;
            }
            if (entries.length() > 0) {
                entries.setLength(entries.length() - 1);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("allStreamUnreadEntries error ").log(ex.getMessage()).end();
            return -1;
        }

        if (System.currentTimeMillis() - start > 100) {
            Logger.error(clz).log("unread entries for stream id ").log(streamId).log(" took longer than 100ms ").end();
        }

        return unreadEntriesCount;
    }

    public static String getAllReadEntries(long profileId, long maxTime, long maxRet, String xmlIds) {
        String query = "SELECT feedreader.getreadentries(" + profileId + ", " + maxTime + "," + maxRet + ",'" + xmlIds
                + "')";
        try {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            }
            return "";

        } catch (SQLException ex) {
            Logger.error(clz).log(query).log(", error ").log(ex.getMessage()).end();
        }

        return "";
    }

    public static ResultSet getAllXmlIdsWithReadMarkers(long streamId, long userId) {
        try {
            String query = "select t2.l_xml_id, t2.t_read_marker from feedreader.userstreamgroups as t0  \n"
                    + "    inner join feedreader.userstreamgroupfeedsubscription as t1  \n"
                    + "        on t0.l_stream_id = t1.l_stream_id \n"
                    + "    inner join feedreader.userfeedsubscriptions as t2  \n"
                    + "        on t2.l_subs_id = t1.l_subs_id where \n" + "\n" + "t0.l_stream_id = " + streamId
                    + "  and t0.l_user_id = " + userId;

            return stmt.executeQuery(query);

        } catch (SQLException e) {
            Logger.error(clz).log(e.getMessage()).end();
        }

        return null;
    }

    public static String getAllXmlIds(long streamId, long userId) {
        try {
            allXmlIdsFromStreamGroup.setLong(1, streamId);
            allXmlIdsFromStreamGroup.setLong(2, userId);
            ResultSet rs = allXmlIdsFromStreamGroup.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next())
                sb.append(rs.getString(1)).append(",");
            if (sb.length() > 0)
                sb.setLength(sb.length() - 1);

            return sb.toString();
        } catch (SQLException ex) {
            Logger.error(clz).log(allXmlIdsFromStreamGroup.toString()).log(", error ").log(ex.getMessage()).end();
        }

        return "";
    }
}
