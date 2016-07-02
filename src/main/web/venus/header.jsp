<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.security.Admin"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.config.Constants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="/pages/header.jsp"></jsp:include>

<div class="col-xs-12" style="margin-bottom: 20px">
		<a href="<%= PageUtils.getAdminBase() %>/index.jsp">home</a> /
		<a href="<%= PageUtils.getAdminBase() %>/pages/add_temp_source.jsp">add temp source</a> /
		<a href="<%= PageUtils.getAdminBase() %>/pages/load_opml.jsp">load opml file</a> /
		<a href="<%= PageUtils.getAdminBase() %>/pages/view_feeds_sources.jsp">view feed sources</a> / 
		<a href="<%= PageUtils.getAdminBase() %>/pages/users.jsp">users</a>
		<h3>Maintenance Scripts</h3>
		<a href="<%= PageUtils.getPath("/jobs/run_fetch.jsp") %>">run fetch</a> /
		<a href="<%= PageUtils.getPath("/jobs/fetch_social_data.jsp") %>">run fetch social data</a> /
		<hr>
</div>
