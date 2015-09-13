<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.UserFeedSubscriptionsTable"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="feedreader.store.Database"%>
<%@page import="feedreader.store.UsersTable"%>
<jsp:include page="../header.jsp"></jsp:include>

<div class="col-xs-5">
<%
	String query = String.format("SELECT * FROM %s LIMIT %d OFFSET %d",
		UsersTable.TABLE, 
		20, 0);
		
	ResultSet rs = Database.rawQuery(query);

    while (rs.next()) {
        String id = rs.getString(DBFields.LONG_USER_ID);
        String displayName = rs.getString(DBFields.STR_SCREEN_NAME);
        String verified = rs.getString(DBFields.BOOL_VERIFIED).toUpperCase();
        String email = rs.getString(DBFields.STR_EMAIL);
        out.append("<a href='?id="+ id +"' onClick=''>"+ displayName +" ("+ verified +") " + email + "</a><br>");
    }
%>
</div>

<div class="col-xs-5">
	<%
		long id = Parameter.asInt(request, "id", 0);
		if (id > 0) {
		    query = String.format("SELECT count(%s) FROM %s WHERE %s = %d", 
		            DBFields.LONG_SUBS_ID,
		            UserFeedSubscriptionsTable.TABLE,
		            DBFields.LONG_USER_ID, id);
		    rs = Database.rawQuery(query);
		    
		    if (rs.next()) {
		        out.append("subscriptions " + rs.getString(1)).append("<br>");
		    }
		    
		}
	%>
</div>

<jsp:include page="/pages/footer.jsp"></jsp:include>

