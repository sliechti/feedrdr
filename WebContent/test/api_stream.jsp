<%@page import="feedreader.cron.CronTimeUtils"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Statement"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.utils.PermissionUtils"%>
<%@page import="feedreader.time.CurrentTime"%>
<%@page import="org.apache.catalina.tribes.util.Arrays"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="feedreader.log.Logger"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="feedreader.api.v1.APIUtils"%>
<%@page import="feedreader.utils.JSONUtils"%>
<%@page import="feedreader.store.Database"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="feedreader.security.Parameter"%>
<%@page contentType="application/json"%>
<%! static class LocalSQL {

        public PreparedStatement stmt1;
        public PreparedStatement stmt2;

        public LocalSQL() {
            try {
                stmt1 = Database.getConnection().prepareStatement(
                        "select t1.l_subs_id, t2.l_xml_id, t2.i_unread, t2.t_unread from feedreader.userstreamgroups as t0 "
                                + " right join feedreader.userstreamgroupfeedsubscription as t1"
                                + "  on t0.l_stream_id = t1.l_stream_id"
                                + " inner join feedreader.userfeedsubscriptions as t2"
                                + "  on t2.l_subs_id = t1.l_subs_id" + " where t0.l_stream_id = ? ");

                stmt2 = Database.getConnection().prepareStatement(
                        "select t0.l_entry_id from feedreader.userfeedentriesinfo as t0 "
                                + " left join feedreader.feedentries as t1 " + "     on t0.l_entry_id = t1.l_entry_id "
                                + " where  " + "     t0.l_profile_id = ? and t0.b_read = true "
                                + "     and t1.t_pub_date > ? and t1.l_xml_id in (?)");
            } catch (Exception e) {
                Logger.info("").log(e.getMessage()).end();
            }
        }
    }

    static LocalSQL local = new LocalSQL();%>
<%
    StringBuilder sb = new StringBuilder();
    sb.setLength(0);

    long pageStart = CurrentTime.inGMT();
    long profileId = Session.asLong(session, Constants.SESSION_SELECTED_PROFILE_ID, 0);
    int entriesPerPage = 10;
    //    int paging = Parameter.asInt(request, "page", 0);
    long streamId = Parameter.asLong(request, "id", 0);
    int offset = Parameter.asInt(request, "offset", 0);
    int userType = Parameter.asInt(request, "userType", 2);
    int count = Parameter.asInt(request, "count", 0);

    long lastRead = Parameter.asLong(request, "l", 0L);
    long firstRead = Parameter.asLong(request, "f", 0L);

    LocalSQL local = new LocalSQL();

    local.stmt1.setLong(1, streamId);
    ResultSet rs = local.stmt1.executeQuery();

    //    ResultSet rs = Database.rawQuery("select t1.l_subs_id, t2.l_xml_id from feedreader.userstreamgroups as t0 "
    //            + " right join feedreader.userstreamgroupfeedsubscription as t1"
    //            + "  on t0.l_stream_id = t1.l_stream_id"
    //            + " inner join feedreader.userfeedsubscriptions as t2"
    //            + "  on t2.l_subs_id = t1.l_subs_id"
    //            + " where t0.l_stream_id = " + streamId );
    long maxTime = CronTimeUtils.getMaxHistory(userType);

    Statement stmt = Database.getStatement();
    String query = "";
    ArrayList<Long> aXmlIds = new ArrayList<Long>();
    while (rs.next()) {

        String xmlId = rs.getString(DBFields.LONG_XML_ID);
        sb.append(xmlId).append(",");
        aXmlIds.add(rs.getLong(DBFields.LONG_XML_ID));
    }
    if (sb.length() == 0) {
        out.write(JSONUtils.error(0, "no subscriptions found for source id " + streamId));
        return;
    }
    sb.setLength(sb.length() - 1); // remove last ,
    String xmlIds = sb.toString();

    long sqlStart = CurrentTime.inGMT();
    query = "select sum(i_total_entries), sum(i_count_0), sum(i_count_1), sum(i_count_2)"
            + "from feedreader.feedsources where l_xml_id in (" + xmlIds + ")";
    rs = Database.rawQuery(query);
    long sqlEnd = CurrentTime.inGMT();
    long execCount = sqlEnd - sqlStart;

    int totalEntries = 0;
    int totalForUser = 0;
    int totalEntriesForUserType0 = 0;
    int totalEntriesForUserType1 = 0;
    int totalEntriesForUserType2 = 0;
    if (rs.next()) {
        totalEntries = rs.getInt(1);
        totalEntriesForUserType0 = rs.getInt(2);
        totalEntriesForUserType1 = rs.getInt(3);
        totalEntriesForUserType2 = rs.getInt(4);

        switch (userType) {
        case FeedAppConfig.USER_0_VAL:
            totalForUser = totalEntriesForUserType0;
            break;
        case FeedAppConfig.USER_1_VAL:
            totalForUser = totalEntriesForUserType1;
            break;
        case FeedAppConfig.USER_2_VAL:
            totalForUser = totalEntriesForUserType2;
            break;
        }
    }

    query = "select t0.l_entry_id from feedreader.userfeedentriesinfo as t0 "
            + " left join feedreader.feedentries as t1 " + "     on t0.l_entry_id = t1.l_entry_id "
            + " where  " + "     t0.l_profile_id = " + profileId + " and t0.b_read = true "
            + "     and t1.t_pub_date > " + maxTime + " and t1.l_xml_id in (" + xmlIds + ")";

    //    local.stmt2.setLong(1, profileId);
    //    local.stmt2.setLong(2, maxTime);
    //    local.stmt2.setArray(3, Database.getConnection().createArrayOf("long", aXmlIds.toArray()));
    //    
    //    Logger.info("read sql " ).log(local.stmt2.toString()).end();

    sqlStart = CurrentTime.inGMT();
    rs = Database.rawQuery(query);
    //    Logger.info(local.stmt2.toString()).end();
    //    rs = local.stmt2.executeQuery();
    sqlEnd = CurrentTime.inGMT();
    long execRead = sqlEnd - sqlStart;

    sb.setLength(0);

    int readCount = 0;
    while (rs.next()) {
        sb.append(rs.getString(DBFields.LONG_ENTRY_ID)).append(",");
        readCount++;
    }
    if (sb.length() > 0)
        sb.setLength(sb.length() - 1);

    String readEntries = sb.toString();
    //Logger.info("").log("read entries ").log(readEntries).end();

    // updating ranges.
    //    if (firstRead != 0 || lastRead != 0)
    //    {
    //        if (firstRead == 0 || lastRead == 0) {
    //            out.write(JSONUtils.count(0));
    //            return;
    //        }
    //        
    //        String timeFilter = "s:" + firstRead + ",e:" + lastRead;
    //        if (!timeFilter.isEmpty()) {
    //            rt.add(timeFilter);
    //        }
    //        
    //        query = "update feedreader.userstreamgroups set s_group_time_filter = '" + rt.serialize() + "' "
    //                + " where l_stream_id = " + streamId;
    ////        Logger.info("updated table : ").log(rt.serialize()).end();
    ////        Logger.info("updated table : ").log(rt.toString()).end();
    //        
    //        out.write(JSONUtils.count(Database.executeUpdate(query)));
    //        return;
    //    }

    sb.setLength(0);
    sb.append("{");

    int totalUnread = (totalEntries - readCount);
    int totalUnreadUser = (totalForUser - readCount);
    int unread0 = (totalEntriesForUserType0 - readCount);
    int unread1 = (totalEntriesForUserType1 - readCount);
    int unread2 = (totalEntriesForUserType2 - readCount);

    sb.append("\"susbcriptions\" : [" + xmlIds + "],");
    sb.append("\"maxTime\" : \"" + new Date(maxTime) + "\", ");
    sb.append("\"maxTimeRaw\" : \"" + maxTime + "\", ");

    sb.append("\"totalEntries\" : \"" + totalEntries + "\", ");
    sb.append("\"readCount\" : \"" + readCount + "\", ");
    sb.append("\"unread\" : \"" + totalUnreadUser + "\", ");
    sb.append("\"unreadCount\" : \"" + totalUnread + "\", ");
    sb.append("\"unread0\" : \"" + unread0 + "\", ");
    sb.append("\"unread1\" : \"" + unread1 + "\", ");
    sb.append("\"unread2\" : \"" + unread2 + "\", ");

    query = "select l_xml_id, l_entry_id, s_link, s_title, t_pub_date from feedreader.feedentries "
            + "where l_xml_id in ( " + xmlIds + ") " + " and t_pub_date > " + maxTime;
    if (!readEntries.isEmpty()) {
        query += " and l_entry_id NOT IN ( " + readEntries + " ) ";
    }

    query += " order by t_pub_date desc";
    query += " limit " + entriesPerPage + " offset " + offset;
    //
    //    // TODO: If end of range is > or = time end.
    //    // Set new time end for this feed to the start of range. 
    //    // Call it read marker and we now there are no unread after it.
    //    Logger.info("").log(query).end();
    //    
    sqlStart = CurrentTime.inGMT();
    rs = Database.rawQuery(query);
    sqlEnd = CurrentTime.inGMT();
    long pageEnd = CurrentTime.inGMT();
    sb.append("\"execCount\" : " + execCount + ", ");
    sb.append("\"execRead\" : " + execRead + ", ");
    sb.append("\"execEntries\" : " + (sqlEnd - sqlStart) + ", ");
    sb.append("\"pageTime\" : " + (pageEnd - pageStart) + ", ");
    sb.append("\"profileId\" : " + profileId + ", ");
    sb.append("\"entries\" : [");
    APIUtils.wrapObject(sb, rs);
    sb.append("]}");
    //    Logger.info(this.getClass()).log("page time ").log((pageEnd - pageStart)).log("ms").end();
%>

<%=sb.toString()%>