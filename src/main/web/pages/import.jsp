<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="java.util.List"%>
<%@page import="java.util.List"%>
<%@page import="feedreader.store.UserProfilesTable"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="feedreader.utils.HtmlStackTrace"%>
<%@page import="org.xml.sax.SAXException"%>
<%@page import="feedreader.utils.HtmlStackTrace"%>
<%@page import="javax.xml.parsers.ParserConfigurationException"%>
<%@page import="feedreader.opml.OPMLParser"%>
<%@page import="feedreader.opml.UserOPMLImportHandler"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="feedreader.utils.FormUploadHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItemStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItemIterator"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="feedreader.utils.HelpUtils"%>

<%@include file="header.jsp" %>

<%!
    // Form fields.
    static final String SINGLE_FEED_NAME        = "feed_name";
    static final String SINGLE_FEED_URL         = "feed_url";
    static final String OPML_FILE               = "opml_file";
    static final String TO_PROFILE              = "to_profile";
    static final String SELECTED_PROFILES       = "selected_profiles";
    static final String ONLYSELECTED            = "onlyselected";
    static final String ALL                     = "all";
    static final String CURRENT                 = "current";
%>

<%
%>

<div id="import" class="col-xs-8">
    <div class="row">
        <div id="opml" class="col-xs-12 form-group">
            <form method="POST" role="form" action="<%= request.getRequestURI() %>" enctype="multipart/form-data">
                <h4>OPML file</h4>
                <label for="<%= TO_PROFILE %>">Import OPML folders as stream groups for </label><br>
                <input type="radio" name="<%=  TO_PROFILE %>" value="all" checked>all profiles<br>
                <input id="only" type="radio" name="<%=  TO_PROFILE %>" value="<%= ONLYSELECTED %>">
                	selected profiles only (
                	<a href="" onclick="showCreateNewProfile(); return false;">new <span class="glyphicon glyphicon-plus text-right"></span></a>
                	)<br>
                <select id="multi" multiple name="selected_profiles" onclick="$('#only').prop('checked', 'true')" style="width: 50%">
                    <% for (ProfileData p : profiles) { %>
                    <option value="<%= p.getProfileId() %>"><%= p.getName() %></option>
                    <% } %>
                </select>
                <br>
                <br>
                <input type="file" name="<%= OPML_FILE %>"><br>
                <input type="submit" class="btn btn-default" name="import_opml" value="Import OPML file"  style="width: 200px">
            </form>
        </div>

    </div>

</div>

<jsp:include page="footer.jsp"></jsp:include>
