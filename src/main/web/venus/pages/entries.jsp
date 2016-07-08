<%@page import="java.util.Date"%>
<%@page import="feedreader.utils.StringUtils"%>
<%@page import="feedreader.utils.HtmlUtils"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="feedreader.store.FeedEntriesTable"%>
<%@page import="feedreader.security.Parameter"%>

<%@include file="/security.jsp" %>

<%= Parameter.asString(request, "id", "noId") %><br>

<%
long id = Parameter.asInt(request, "id", 0);

ResultSet e = FeedEntriesTable.getEntries(id);
int count = 0;
while (e.next())
{
    out.append(""+new Date(e.getLong(DBFields.TIME_PUBLICATION_DATE))).append(" - ");
    out.append("<a href='").append(e.getString(DBFields.STR_LINK)).append("' targer='_parent'>")
            .append(StringUtils.cut(e.getString(DBFields.STR_TITLE), 80))
            .append("</a><br>");
    count++;
}

out.append("count " + count).append("<br>");

%>

<jsp:include page="/pages/footer.jsp"></jsp:include> >