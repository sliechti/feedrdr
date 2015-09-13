<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.main.AppContextInit"%>
<%@page import="feedreader.security.UserSession"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.security.Admin"%>
<%@page import="feedreader.parser.XmlFeedParser"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.net.URI"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="feedreader.store.XmlAttrTable"%>
<%@page import="feedreader.store.FeedSourcesTable"%>
<%@page import="java.util.Date"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.store.FeedSourceChannelImageTable"%>
<%@page import="feedreader.store.FeedSourceChannelDataTable"%>
<%@page import="feedreader.entities.XmlChannelImage"%>
<%@page import="feedreader.entities.XmlChannelData"%>
<%@page import="feedreader.entities.FeedSourceEntry"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="feedreader.feed.utils.Fetch"%>
<%@page import="feedreader.feed.utils.FetchHandler"%>
<%@page import="feedreader.time.CurrentTime"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.utils.FormUtils"%>

<jsp:include page="../pages/header.jsp"></jsp:include>

<div class="col-xs-12">

<%
	long userId = UserSession.getUserId(request);
	if (userId == 0) {
	    PageUtils.gotoStart(response);
	}
	
	UserData user = UsersTable.get(userId);
	if (!user.isAdmin()) {
	    PageUtils.gotoStart(response);
	}
%>
<head>
    <% if (Parameter.asBoolean(request, "keepRefreshing", false)) { %>
    <meta http-equiv="refresh" content="<%= Parameter.asString(request, "refreshrate", "1") %>">
    <% } %>
</head>

<script type="text/javascript">
    function reset() 
    {
        document.getElementById("xmlid").value = "";
        document.getElementById("url").value = "";
        document.forms[0].submit();
    }
</script>
<form method="GET" action="">
    <input type="checkbox" name="run"<%= FormUtils.checked(request, "run") %>><label>run</label><br>
    <input type="checkbox" name="delete"<%= FormUtils.checked(request, "delete") %>><label>delete entries, reinsert</label><br>
    <input type="checkbox" name="dryRun"<%= FormUtils.checked(request, "dryRun") %>><label>dry run</label><br>
    <input type="checkbox" name="force check"<%= FormUtils.checked(request, "forceCheck") %>><label>force check (not used yet)</label><br>
    <input type="checkbox" name="validationrun"<%= FormUtils.checked(request, "validationrun") %>><label>validation run</label><br>
    <input type="checkbox" name="keepRefreshing"<%= FormUtils.checked(request, "keepRefreshing") %>><label>keep refreshing</label><br>
    <hr>
    <input type="text" size="4" id="refreshrate" name="refreshrate" value="<%= Parameter.asString(request, "refreshrate", "1") %>"><label>refresh rate</label><br>
    <input type="text" size="4" id="xmlid" name="xmlid" value="<%= Parameter.asString(request, "xmlid", "0") %>"><label>xml id</label><br>
    <input type="text" size="20" id="url" name="url" value="<%= Parameter.asString(request, "url", "") %>"><label>xml url</label><br>
    <hr>
    <input type="submit" value="run">
    <a href="" onclick="reset(); return false;">reset</a>
</form>

<% 
if (!Parameter.asBoolean(request, "run", false)) {
    return;
}

String baseUrl = request.getContextPath();
%>

<pre style="width: 90%">
<%
    String  url      = Parameter.asString(request, "url", "");
    long xmlId = Parameter.asLong(request, "xmlid", 0);
    boolean force    = Parameter.asBoolean(request, "force", false);
    boolean delete    = Parameter.asBoolean(request, "delete", false);
    boolean validationrun    = Parameter.asBoolean(request, "validationrun", false);

    FetchHandler handler = new FetchHandler();
    handler.setForceDelete(delete);
    
    if (FeedAppConfig.XML_SAVE) {
        
        Fetch.setDownloadPath(AppContextInit.getDownloadXmlPath());
    }
    
    int code = 0;
    
    if (validationrun) {   
        code = Fetch.validationRun(handler);
    } else if (xmlId > 0) {
        out.append("fetchig with id ").append(""+xmlId).append("<br>");
        code = Fetch.run(handler, xmlId, force);
    } else if (!url.isEmpty()) {
        out.append("fetchig with url ").append(url).append("<br>");
        code = Fetch.run(handler, url, force);
    } else {
        code = Fetch.run(handler);
    }
    
    if (url.isEmpty() && handler.getSourceEntry() != null)
    {
        url = handler.getSourceEntry().getXmlUrl();
    }

    if (!url.isEmpty())
    {
        out.write("<a href=\"" + request.getRequestURI() + "\">next</a> | ");
        out.write("<a target=\"_blank\" href=\"" + url + "\">visit</a> | ");
        out.write("<a href=\"" + request.getRequestURI() + "?url=" + url + "\">retry</a> | ");
        out.write("<a href=\"" + request.getRequestURI() + "?url=" + url + "&force=true\">force</a> |");
        out.write("<br><br>");
    }
    
    FeedSourceEntry entry = handler.getSourceEntry();
    
    switch(code)
    {
        case Fetch.RetCode.NO_SOURCES_FOUND:
            out.write("No sources found.");
            break;

        case Fetch.RetCode.CHECKING_NEXT_SOURCE_IN:
            out.write("Next check at " + FeedAppConfig.DATE_FORMAT.format(new Date(Fetch.nextCheckAt)) +
                    " ... in " + ((Fetch.nextCheckAt - CurrentTime.inGMT()) / 1000) +
                    "s<br><br>");
            break;

        case Fetch.RetCode.NEXT_CHECK_IN:
            out.write("Next check at: " +  FeedAppConfig.DATE_FORMAT.format(new Date(entry.getCheckedAt())) +
                    ", use force if needed.<br>");
            break;

        case Fetch.RetCode.VALID:
            FeedSourcesTable.setValid(entry.getId());
            out.write("<b>valid source</b><br>");
            
        case Fetch.RetCode.FINISHED:
        {
            XmlFeedParser parser = handler.getParser();

            if (FeedAppConfig.XML_GATHER_INFO) {
                XmlAttrTable.save(entry.getXmlUrl(), parser.nodeList);
            }
            
            out.write("url processed : " + entry.getId() + "/" + entry.getXmlUrl() + "<br>");
            out.write("added   at    : " + new Date(entry.getAddedAt()) + "<br>");
            out.write("checked at    : " + new Date(entry.getCheckedAt()) + "<br>");
            out.write("<br>");
            
            out.write("<a target=\"_blank\" href="+ baseUrl +"/pages/reader.jsp#/s/" + entry.getId() + ">view in reader</a><br>");
                    
            XmlChannelData channelData = parser.getChannelData();
            XmlChannelImage channelImg = parser.getChannelImage();

            out.write("channel Data  : " + channelData + "<br>");
            out.write("        Image : " + channelImg + "<br><br>");

            FeedSourceChannelDataTable.save(entry.getId(), channelData, true);
            FeedSourceChannelImageTable.save(entry.getId(), channelImg, true);
            
            if (handler.getSavedCount() > 0)
            {
                // TODO: Update
//                    if (!FeedSourceChannelDataTable.save(entry.getId(), channelData)) {
//                        out.write("Error saving channel data. <br>");
//                    }
//
//                    if (FeedSourceChannelImageTable.save(entry.getId(), channelImg)) {
//                        out.write("Error saving image data. <br>");
//                    }
            }
            
            out.write("Saved         : " + handler.getSavedCount() + "<br>");
            out.write("Not changed   : " + handler.getNotChanged() + "<br>");
            out.write("Entries found : " + handler.getFound() + "<br>");
            out.write("Validated     : " + handler.getValid() + "<br><br>");
            out.write("Total count   :<b> " + handler.getTotalCount() + "</b><br>");
        }
            break;

        default:
            out.write("Error " + Fetch.stringCode(code) + "<br>");
            out.write(entry.toString() + "<br>");
    }
%>

</pre>

</div>
