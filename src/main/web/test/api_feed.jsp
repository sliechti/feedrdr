<%@page contentType="application/json"%>
<%@page import="feedreader.log.Logger"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="feedreader.api.v1.APIUtils"%>
<%@page import="feedreader.utils.JSONUtils"%>
<%@page import="feedreader.utils.RangeTimes" %>
<%@page import="feedreader.utils.RangeTimes.Range" %>
<%@page import="feedreader.store.Database"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="feedreader.security.Parameter"%>
<%! static StringBuilder sb = new StringBuilder(); 
static Calendar c = Calendar.getInstance(Locale.getDefault());
%>
<%
    sb.setLength(0);

    int entriesPerPage = 5;
    int paging = Parameter.asInt(request, "page", 0);
    long xmlId = Parameter.asLong(request, "id", 0);
    int userType = Parameter.asInt(request, "userType", 0);

    long lastRead = Parameter.asLong(request, "l", 0L);
    long firstRead = Parameter.asLong(request, "f", 0L);

    c.setTime(new Date());

    String query = "select t1.l_subs_id, t1.s_time_filter from feedreader.feedsources as t0 "
            + "inner join feedreader.userfeedsubscriptions as t1 "
            + "on t0.l_xml_id = t1.l_xml_id where t0.l_xml_id = " + xmlId;

    ResultSet rs = Database.rawQuery(query);

    if (!rs.next()) {
        out.write(JSONUtils.error(0, "no subscription found for source id " + xmlId));
        return;
    }

    long subsId = rs.getLong(DBFields.LONG_SUBS_ID);
    String dbFilter = rs.getString(DBFields.STR_TIME_FILTER);

    RangeTimes rt = new RangeTimes();

    if (!dbFilter.isEmpty()) {
        rt.add(dbFilter);
    }

    // updating ranges.
    if (firstRead != 0 && lastRead != 0) {
        String timeFilter = "s:" + firstRead + ",e:" + lastRead;
        if (!timeFilter.isEmpty()) {
            rt.add(timeFilter);
        }

        query = "update feedreader.userfeedsubscriptions set s_time_filter = '" + rt.serialize() + "' "
                + " where l_subs_id = " + subsId;
        out.write(JSONUtils.count(Database.executeUpdate(query)));
        return;
    }

    switch (userType) {
    case 1:
        c.add(Calendar.DAY_OF_YEAR, -14);
        break;

    case 2:
        c.add(Calendar.MONTH, -1);
        break;

    default:
    case 0:
        c.add(Calendar.DAY_OF_YEAR, -3);
        break;
    }

    long maxTime = c.getTimeInMillis();

    sb.append("{");

    sb.append("\"maxTime\" : \"" + new Date(maxTime) + "\", ");
    sb.append("\"maxTimeRaw\" : \"" + maxTime + "\", ");

    query = "select l_entry_id, s_link, s_title, t_pub_date from feedreader.feedentries " + "where l_xml_id = "
            + xmlId + " and t_pub_date > " + maxTime;
    if (rt.getRangeCount() > 0) {
        for (Range r : rt.getRanges()) {
            query += " AND l_entry_id NOT IN "
                    + " (select l_entry_id from feedreader.feedentries where l_xml_id = " + xmlId + " AND ("
                    + r.toSqlString(DBFields.TIME_PUBLICATION_DATE) + "))";
        }
    }

    query += " order by t_pub_date desc";
    query += " limit " + entriesPerPage + " offset " + (paging * entriesPerPage);

    // TODO: If end of range is > or = time end.
    // Set new time end for this feed to the start of range. 
    // Call it read marker and we now there are no unread after it.
    Logger.notice("").log(query).end();

    rs = Database.rawQuery(query);
    sb.append("\"entries\" : [");
    APIUtils.wrapObject(sb, rs);
    sb.append("]}");
%>

<%=sb.toString()%>