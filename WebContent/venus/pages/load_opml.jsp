<%@page import="feedreader.opml.OPMLFeedSourcesCallback"%>
<%@page import="feedreader.utils.FormUploadHelper"%>
<%@page import="org.xml.sax.SAXException"%>
<%@page import="feedreader.utils.HtmlStackTrace"%>
<%@page import="javax.xml.parsers.ParserConfigurationException"%>
<%@page import="feedreader.log.Logger"%>
<%@page import="feedreader.store.RetCodes"%>
<%@page import="feedreader.store.FeedSourcesTable"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="feedreader.opml.OPMLParser"%>
<%@page import="java.io.InputStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItemStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItemIterator"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>

<jsp:include page="../header.jsp"></jsp:include>

<%!
    static final String OPML_FORM_FIELD = "import_opml";
%>

<div id="opml" style="position: relative; float: left; width: 400px;">
<form method="POST" action="<%= request.getRequestURI() %>" enctype="multipart/form-data">
    <h4>OPML file</h4>
    <input type="file" name="<%= OPML_FORM_FIELD %>">
    <input type="submit" name="import_opml" value="Import OPML file"  style="width: 200px">
</form>
</div>

<%
    if (FormUploadHelper.isMultiPartContent(request))
    {
        FormUploadHelper helper = new FormUploadHelper(request);
        OPMLFeedSourcesCallback handler = new OPMLFeedSourcesCallback();
        
        try
        {
            OPMLParser parser = new OPMLParser(handler);
            parser.parse(new ByteArrayInputStream(helper.asStream(OPML_FORM_FIELD).toByteArray()));

            out.write("<hr>");
            out.append(handler.toString()).append("<br>");
        }
        catch (ParserConfigurationException e)
        {
            HtmlStackTrace.printRed(out, e.getStackTrace(), 10, "<br>");
        }
        // TODO: Report error. 
        catch (SAXException e)
        {
            Logger.error(this.getClass()).log("error importing OPML file: ").log(e.getException()).end();
            HtmlStackTrace.printRed(out, e.getStackTrace(), 10, "<br>");
            out.write("<h1>Error parsing XML: </h1>");
            out.write(helper.asStream(OPML_FORM_FIELD).toString().replace("<", "&lt;"));
        }
    }
%>

<jsp:include page="/pages/footer.jsp"></jsp:include>
