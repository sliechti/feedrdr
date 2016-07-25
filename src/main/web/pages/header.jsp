
<%@page import="feedreader.utils.ApplicationConfig"%>
<%@page import="feedreader.config.OAuthConfig"%>
<%@page import="feedreader.oauth.OAuthType"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.store.UserProfilesTable"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="java.util.Date"%>
<%@page import="feedreader.time.DateUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.security.UserSession"%>

<%@include file="/security.jsp" %>

<%
    String baseUrl = FeedAppConfig.BASE_APP_URL;

    String minify = request.getParameter("minify");
    boolean isLocal = ApplicationConfig.instance().isLocal();
    String minjs = (!isLocal && minify == "1" ? ".min.js" : ".js");
    String mincss = (!isLocal ? ".min.css" : ".css");

    request.setAttribute("baseUrl", FeedAppConfig.BASE_APP_URL);

    if (request.getRequestURI().contains("reader.jsp")) {
        request.setAttribute("logoAction", "openLeftBar(); return false;");
        request.setAttribute("logoUrl", "");
    } else {
        request.setAttribute("logoAction", "");
        request.setAttribute("logoUrl", PageUtils.getHome());
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
        <meta name="appVersion" content="<%= FeedAppConfig.APP_VERSION %>">
        <title><%= FeedAppConfig.APP_NAME %></title>

        <link href="<%= PageUtils.getPath("/css/bootstrap.min.css") %>" rel="stylesheet" type="text/css"/>
        <link href="<%= PageUtils.getPath("/css/default.css") %>" rel="stylesheet" type="text/css"/>

        <script src="<%= PageUtils.getPath("/js/jquery.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/jquery.visible.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/handlebars.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/bootstrap.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/director.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/global" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/profiles" + minjs) %>" type="text/javascript"></script>
    <% if (request.getAttribute("j") != null) { %>
        <script src="<%= PageUtils.getPath("/js/jlinq.min.js") %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("s") != null) { %>
        <script src="<%= PageUtils.getPath("/js/subscriptions" + minjs) %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("r") != null) { %>
        <script src="<%= PageUtils.getPath("/js/reader.handlebars" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/reader" + minjs) %>" type="text/javascript"></script>
        <% if (user.isAdmin()) { %>
        	<script src="<%= PageUtils.getPath("/js/reader.admin" + minjs) %>" type="text/javascript"></script>
        <% }%>
    <% } %>
    <% if (request.getAttribute("rs") != null) { %>
        <script src="<%= PageUtils.getPath("/js/reader.subscriptions" + minjs) %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("e") != null) { %>
        <script src="<%= PageUtils.getPath("/js/settings" + minjs) %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("c") != null) { %>
        <script src="<%= PageUtils.getPath("/js/collections" + minjs) %>" type="text/javascript"></script>
    <% } %>
        <script src="<%= PageUtils.getPath("/js/jscolor/jscolor.min.js") %>" type="text/javascript"></script>
    <% if (user.getAuthType() != OAuthType.NONE)  { %>
        <script src="<%= PageUtils.getPath("/js/hello.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/hello.init.min.js") %>" type="text/javascript"></script>
    <% } %>

        <script type="text/javascript">
            setBaseUrl("<%= request.getContextPath() %>");

    <% if (user.getAuthType() != OAuthType.NONE)  { %>
            initHello('<%= OAuthConfig.FB_KEY %>',
                      '<%= OAuthConfig.GOOGLE_KEY %>',
                      '<%= OAuthConfig.LIVE_KEY %>');

            var network = '';

            setHelloCallbacks(
                function(data) {
                	/* noop onAuth */
                },
                function(data) { // on login
                    network = data.network;
                },
                function(data) { // on logout.
                    location.href = baseUrl + '/logout.jsp';
                });
            function logout() {
                hello.logout(network);
                $.get(baseUrl + '/logout.jsp', function(){});
            }
    <%  } else { %>
        function logout() {
            location.href = baseUrl + '/logout.jsp';
        }
    <% } %>
        </script>
			<style>

			</style>
</head>

    <body>
	<nav class="navbar">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#reader-nav">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${logoUrl}" onclick="${logoAction}">
					<img src="${baseUrl}/img/logo.svg" height="20px" />
				</a>
			</div>
			<div class="collapse navbar-collapse" id="reader-nav">
				<ul class="nav navbar-nav">
					<% if (user.isGenerated()) { %>
					<li><a href="wizard">Wizard</a></li>
					<% } %>
					<li><a href="<%=PageUtils.getPath("/pages/collections.jsp")%>">Collections</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
						<span id="profile" data-id="<%=profile.getProfileId()%>"><%=profile.getName()%></span>
						<ul id="profiles" class="dropdown-menu" role="menu">
							<li><a href="<%=PageUtils.getPath("/pages/collections.jsp")%>">Collections</a></li>
						</ul>
					</li>
					<li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                            <span class="underline"><%= user.getScreenName() %>
                            	<% if (user.isAdmin()) {%>[admin]<% } %>
                            	<% if (!user.isVerified()) {%>[unverified]<% } %>
                                <%//= userType %><span class="caret"></span></span></a>

                        <ul class="dropdown-menu" role="menu">
                            <li><div class="navbar-div block">
                                    <a href="" onclick="logout(); return false;">Sign out
                                    <span class="glyphicon glyphicon-log-out text-right"></span></a>
                                </div></li>
                            <li><div class="navbar-div block">
                                <a href="<%= PageUtils.getPath("/pages/settings.jsp") %>">User settings
                                    <span class="glyphicon glyphicon-cog text-right"></span></a>
                                </div>
                            </li>
                            <li>
                                <div class="navbar-div block">
                                    <a href="<%= baseUrl %>/pages/settings.jsp#/v/pro">Profile settings <span class="glyphicon glyphicon-cog text-right"></span></a>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
			</div>
		</div>
            <div id="loader" class="profileColor" style="height: 1px; "></div>
	</nav>


        <%@include file="tmpl/profiles.tmpl.jsp" %>

        <div id="div_new_profile" class="noshow text-center">
            <div class="modal-body">
                <input type="text" name="profile_name" value="" placeholder="Profile's name">
                <input type="text" name="picker">
                <div id="modal_error" style="margin-top: 10px" class="noshow alert alert-danger">
                    <button type="button" class="close" onclick="$('#modal_error').hide();">
                        <span aria-hidden="true">×</span><span class="sr-only">Close</span>
                    </button>
                    <p id="modal_error_text"></p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="createNewProfile('profile_name', 'picker')">Create Profile</button>
              </div>
        </div>
        <div id="content" class="container-fluid">
        <div class="row">


