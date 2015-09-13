<%@page import="feedreader.store.DBFields"%>
<%@page import="feedreader.entities.XmlChannelImage"%>
<%@page import="feedreader.entities.XmlChannelData"%>
<%@page import="feedreader.feed.utils.Fetch"%>
<%@page import="feedreader.feed.utils.FeedValidationHandler"%>
<%@page import="feedreader.store.RetCodes"%>
<%@page import="feedreader.store.FeedSourcesTable"%>
<%@page import="feedreader.security.Parameter"%>

<%@include file="../header.jsp" %>

<form method="POST" action="">    
    <input type="text" name="url">
    <input type="submit" value="add">
</form>

<%
    out.append("<hr>");
    
    if (request.getMethod() == "POST") 
    {
        String url = Parameter.asString(request, "url", "");
        out.append("adding new source ").append(url).append("<br>");

        switch(FeedSourcesTable.addNewSource(url))
        {
            case QUEUED:
                out.append("source queued").append("<br>");
                break;
                
            case IN_QUEUE:
                out.append("source already known").append("<br>");
                break;
                
            default:
                out.append("error while adding source").append("<br>");
        }
    }
%>


<%@include file="/pages/footer.jsp" %>

