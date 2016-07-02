<%@page import="feedreader.utils.PageUtils"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="java.io.PrintStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.utils.HtmlStackTrace"%>
<%@page import="java.util.Arrays"%>
<%@page import="feedreader.utils.SimpleMail"%>
<%@page isErrorPage="true" %>

<%
	String message =  exception.getMessage().replace("\n", "");
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(os);
	exception.printStackTrace(ps);
	String stackTrace = os.toString().replace("\n", "\n<br>");
%>
<% if (Environment.isDev()) { %>

<link href="<%= PageUtils.getPath("/css/bootstrap.css") %>" rel="stylesheet" type="text/css"/>

<center><p style="padding: 30px; background: #DCE9E0; text-align: left; width: 1200px; font-family: monospace; font-size: 14px;">
<%
    out.append("<b>" + message + "</b><br>");

	out.append(stackTrace);

%>
</p></center>

</div>

<% } else { %>

<% 
    SimpleMail mail = new SimpleMail();
    mail.send("stacktrace@feedrdr.co", "StackTrace", 
            	"steven@feedrdr.co", "Steven Liechti", 
            	"StackTrace " + Environment.name(), message + "" + stackTrace);
%>

<center>
<div style="font-family: sans-serif; border: 1px #2E6DA4 solid; font-size: 130%; width: 700px; margin: auto; padding: 10px;">
	<h3 style="background-color: #337AB7; color: white">Web server error</h3>
	<p>There was an error with the page you requested. We will receive a message and try to fix this issue as soon as
	possible.</p>
	<p><a href="/<%= FeedAppConfig.BASE_APP_URL %>">Go back to the homepage.</a></p>
</div></center>

<% } %>