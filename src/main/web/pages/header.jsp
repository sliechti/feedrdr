<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
    request.setAttribute("isLocal", isLocal);

    request.setAttribute("baseUrl", FeedAppConfig.BASE_APP_URL);

	request.setAttribute("logoAction", "openLeftBar(); return false;");
	request.setAttribute("logoUrl", "");

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
        <script src="<%= PageUtils.getPath("/js/vendor/jquery.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/vendor/jquery.visible.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/vendor/handlebars.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/vendor/bootstrap.min.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/vendor/director.min.js") %>" type="text/javascript"></script>
    <% if (request.getAttribute("j") != null) { %>
        <script src="<%= PageUtils.getPath("/js/vendor/jlinq.min.js") %>" type="text/javascript"></script>
    <% } %>
        <script src="<%= PageUtils.getPath("/js/jscolor/jscolor.min.js") %>" type="text/javascript"></script>


        <script src="<%= PageUtils.getPath("/js/app/global.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/app/profiles.js") %>" type="text/javascript"></script>
    <% if (request.getAttribute("s") != null) { %>
        <script src="<%= PageUtils.getPath("/js/app/subscriptions.js") %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("r") != null) { %>
        <script src="<%= PageUtils.getPath("/js/app/reader.handlebars.js") %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/app/reader.js") %>" type="text/javascript"></script>
        <% if (user.isAdmin()) { %>
        	<script src="<%= PageUtils.getPath("/js/app/reader.admin.js") %>" type="text/javascript"></script>
        <% }%>
    <% } %>
    <% if (request.getAttribute("rs") != null) { %>
        <script src="<%= PageUtils.getPath("/js/app/reader.subscriptions.js") %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("e") != null) { %>
        <script src="<%= PageUtils.getPath("/js/app/settings.js") %>" type="text/javascript"></script>
    <% } %>
    <% if (request.getAttribute("c") != null) { %>
        <script src="<%= PageUtils.getPath("/js/app/collections.js") %>" type="text/javascript"></script>
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
                    location.href = baseUrl + '/logout';
                });
            function logout() {
                hello.logout(network);
                $.get(baseUrl + '/logout', function(){});
            }
    <%  } else { %>
        function logout() {
            location.href = baseUrl + '/logout';
        }
    <% } %>
        </script>
			<style>
			.navbar {
				background-color: white;
				z-index: 200;
			}
			</style>
</head>

    <body>
	<nav class="navbar navbar-fixed-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" onclick="closeLeftBar();" data-toggle="collapse" data-target="#reader-nav">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${logoUrl}" onclick="${logoAction}">
					<img id="logo" src="${baseUrl}/img/logo.svg" height="20px" />
				</a>
			</div>
			<div class="collapse navbar-collapse" id="reader-nav">
				<ul class="nav navbar-nav">
					<% if (user.isGenerated()) { %>
					<li><a href="wizard">Wizard</a></li>
					<% } %>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
							<span id="profile" data-id="<%=profile.getProfileId()%>"><%=profile.getName()%></span>
						</a>
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

<div id="leftbar" class="noshow">
	<c:if test="${!pageContext.request.requestURI.endsWith('/reader.jsp')}">
		<div class="back">
			<a href="${pageContext.request.contextPath}/pages/reader.jsp">back to reader</a>
		</div>
	</c:if>
    <div id="special-entries">
    <ul class="leftmenu-ul">
    	<li>
	        <a href="reader.jsp#/v/a" id="mAll" onclick="closeLeftBar();" style="display: block">All</a>
    	</li>
    	<li>
	        <a href="reader.jsp#/v/s" id="mSaved" onclick="closeLeftBar();" style="display: block">Saved</a>
		</li>
		<li>
			<a href="reader.jsp#/v/r" id="mRr" onclick="closeLeftBar();" style="display: block">Recently read</a>
		</li>

    </ul>
    </div>
    <div id="add-content">
    	<ul class="leftmenu-ul">
    		<li>
		    	<a onclick="closeLeftBar();" href="<%= PageUtils.getPath("/pages/import.jsp") %>" style="display: block">Import</a>
    		</li>
    		<li>
		    	<a onclick="closeLeftBar();" href="<%= PageUtils.getPath("/pages/subscriptions.jsp") %>" style="display: block">Subscriptions</a>
    		</li>
			<li><a href="<%=PageUtils.getPath("/pages/collections.jsp")%>">Collections</a></li>
    	</ul>
    </div>

	<c:if test="${pageContext.request.requestURI.endsWith('/reader.jsp')}">
    <div class="left-icons-container">
        <a title="show all" href="" onclick="showOnlyWithUnread(false); return false;">
        <span class="glyphicon glyphicon-eye-open"></span></a>
        <a title="show only unread" href="" onclick="showOnlyWithUnread(true); return false;">
        <span class="glyphicon glyphicon-eye-close"></span></a>
        <a title="sort A-Z" href="" onclick="sortByAlphabet(2);return false;">
        <span class="glyphicon glyphicon-sort-by-alphabet"></span></a>
        <a title="sort Z-A" href="" onclick="sortByAlphabet(1);return false;">
        <span class="glyphicon glyphicon-sort-by-alphabet-alt"></span></a>
        <a title="sort by unread 9-0" href="" onclick="sortByUnread(1);return false;">
        <span class=" glyphicon glyphicon-sort-by-attributes-alt"></span></a>
        <a title="sort by unread 0-9" href="" onclick="sortByUnread(2);return false;">
        <span class="glyphicon glyphicon-sort-by-attributes"></span></a>
        <a title="refresh" href="" onclick="refreshUnread(2);return false;">
        <span class="glyphicon glyphicon-refresh"></span></a>
    </div>

    <div>
        <div class="left">
            <span class="profileColor">&nbsp;</span>
            STREAMS
        </div>
        <div class="right">
            <a href="" onclick="newStreamGroup(); return false;"><span class="glyphicon glyphicon-plus"></span></a>
        </div>
    </div>
    <div id='menusubs'></div>

	</c:if>
</div>
