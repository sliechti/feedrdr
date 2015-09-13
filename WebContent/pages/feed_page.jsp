<%@page import="feedreader.security.Parameter"%>

<%
out.append(Parameter.asString(request, "id", "none"));
%>
