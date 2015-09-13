<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.Database"%>
<%@page import="feedreader.utils.HtmlUtils"%>
<%@page import="feedreader.utils.StringUtils"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="feedreader.entities.FeedSourceEntry"%>
<%@page import="feedreader.store.FeedSourcesTable"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="../header.jsp"></jsp:include>

<script>
    function select(id) {
        document.getElementById('i').src = 'entries.jsp?id=' + id;
    }
</script>

<div class="col-xs-12">
<form method="GET" action="">
	<input type="checkbox" name="gave_up"> Gave up
	<input type="submit" name="send">
</form>

<% 
boolean gaveUp = Parameter.asBoolean(request, "gave_up", false);

String sql = String.format("SELECT * FROM %s WHERE %s = %b LIMIT 20 OFFSET 0", 
                FeedSourcesTable.TABLE,
                DBFields.BOOL_GAVE_UP, gaveUp);
ResultSet rs = Database.rawQuery(sql);
out.write("sql " + sql);
%>

</div>

<div class="col-xs-5" style="overflow: scroll;">
<%

    while (rs.next()) {
        String id = rs.getString(DBFields.LONG_XML_ID);
        out.append(HtmlUtils.getOnClickLink(request, "select(" + id + "); return false;",
                StringUtils.cut(rs.getString(DBFields.STR_XML_URL), 25)));

        out.append("<br>");
    }
%>
</div>

<div class="col-xs-5">
<iframe id="i" src="entries.jsp" height='400' width='100%' style='margin: auto' />
</div>

<jsp:include page="/pages/footer.jsp"></jsp:include>

