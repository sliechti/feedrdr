   
<%@page import="feedreader.config.OAuthConfig"%>
<%@page import="feedreader.oauth.OAuthType"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.store.UserProfilesTable"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="java.util.Date"%>
<%@page import="feedreader.time.DateUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.security.UserSession"%>

<jsp:include page="/security.jsp"></jsp:include>

<%
	UserData user = (UserData)request.getAttribute("user");
	ProfileData profile = (ProfileData)request.getAttribute("profile");
	
	String baseUrl = FeedAppConfig.BASE_APP_URL;
	
	String minify = request.getParameter("minify");
	String minjs = (Environment.isProd() && minify == "1" ? ".min.js" : ".js");
	String mincss = (Environment.isProd() ? ".min.css" : ".css");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="appVersion" content="<%= FeedAppConfig.APP_VERSION %>">
        <title><%= FeedAppConfig.APP_NAME %></title>
        
        <link href="<%= PageUtils.getPath("/css/bootstrap" + mincss) %>" rel="stylesheet" type="text/css"/>
        <link href="<%= PageUtils.getPath("/css/default" + mincss) %>" rel="stylesheet" type="text/css"/>
        
        <script src="<%= PageUtils.getPath("/js/jquery" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/jquery.visible" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/handlebars" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/bootstrap" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/director" + minjs) %>" type="text/javascript"></script>        
        <script src="<%= PageUtils.getPath("/js/global" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/profiles" + minjs) %>" type="text/javascript"></script>
    <% if (request.getAttribute("j") != null) { %>
        <script src="<%= PageUtils.getPath("/js/jlinq" + minjs) %>" type="text/javascript"></script>
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
        <script src="<%= PageUtils.getPath("/js/jscolor/jscolor" + minjs) %>" type="text/javascript"></script>
    <% if (user.getAuthType() != OAuthType.NONE)  { %>
        <script src="<%= PageUtils.getPath("/js/hello" + minjs) %>" type="text/javascript"></script>
        <script src="<%= PageUtils.getPath("/js/hello.init" + minjs) %>" type="text/javascript"></script>
    <% } %>
    
        <script type="text/javascript">    
            setBaseUrl("<%= request.getContextPath() %>");
            
    <% if (user.getAuthType() != OAuthType.NONE)  { %>            
            initHello('<%= OAuthConfig.getFbKey() %>', 
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
            .profileColor {
                background-color: #<%= profile.getColor() %>;
            }
            .read {
                background-color: #AFE8FF;
            }            
        </style>
    </head>

    <body>       
        <nav id="nav" class="navbar navbar-default navbar-fixed-top" role="navigation" style="z-index: 1;">
            <div class="container">
                <div class="navbar-header">
                    <a href="" style="float: left;" class="navbar-brand noshow show_menu" onclick="showMenu(); return false;"><span class="glyphicon glyphicon-list"></span></a>
                    <a href="<%= PageUtils.getPath("/home.jsp") %>" class="navbar-brand"><span id="homeIcon" class="glyphicon glyphicon-home" aria-hidden="true"></span>&nbsp;<%= FeedAppConfig.APP_NAME_URL %></a>
                </div>
                <ul class="nav navbar-nav hidden-xs">
                    <li>
                        <a href="<%= PageUtils.getPath("/pages/collections.jsp") %>">Collections</a>
                    </li>     
                                      <li>
                        <a id="feedback" href="<%= PageUtils.getPath("/pages/collections.jsp") %>">Feedback</a>
                    </li>   
                </ul>                      
                <ul class="nav navbar-nav navbar-right visible-xs">
                    <li>
                        <div style="margin-left: 15px">
                            <a href="" onclick="showOnlyWithUnread(false); return false;"><span class="glyphicon glyphicon-eye-open"></span></a> | 
                            <a href="" onclick="showOnlyWithUnread(true); return false;"><span class="glyphicon glyphicon-eye-close"></span></a> |
        <a href="" onclick="sortByAlphabet(2);return false;"><span class="glyphicon glyphicon-sort-by-alphabet"></span></a> |
        <a href="" onclick="sortByAlphabet(1);return false;"><span class="glyphicon glyphicon-sort-by-alphabet-alt"></span></a> |
        <a href="" onclick="sortByUnread(1);return false;"><span class=" glyphicon glyphicon-sort-by-attributes-alt"></span></a> |
        <a href="" onclick="sortByUnread(2);return false;"><span class="glyphicon glyphicon-sort-by-attributes"></span></a>
        
                        </div>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Stream Groups
                            <span class="caret"></span></a>                            
                        <ul class="dropdown-menu" role="menu">
                                <div class="navbar-div">
                                    <ul>
                                    <li><a href="#/v/a">All</a></li>
                                    <li><a href="#/v/s">Saved</a></li>
                                    <li><a href="#/v/r">Recently read</a></li>
                                    </ul>
                                </div>
                                <div class="navbar-div" id="small_menusubs"></div>
                        </ul>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                            
                            <span class="glyphicon glyphicon-user"></span>&nbsp;
                            <span id="profile" data-id="<%= profile.getProfileId() %>"><%= profile.getName() %></span><span class="caret"></span></a>
                            <ul id="profiles" class="dropdown-menu" role="menu"></ul>
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
                            <li>
                        		<a href="" onClick="showClassicWidget(); return false;">Feedback</a>
                    		</li>                               
                        </ul>
                    </li>
                </ul>
            </div>
            <div id="loader" class="profileColor" style="height: 1px; "></div>
        </nav>
        <div class="visible-xs" style="background-color: #f5f5f5; position: fixed; left: 15px; top: 15px; z-index: 999">
            <a id="smallMenu" href="" onclick="toogleSmallMenu(); return false;"><span class="glyphicon glyphicon-align-justify"></span></a>
        </div>
                                
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
        <!-- UserVoice JavaScript SDK (only needed once on a page) -->
        <div id="content" style="margin-top: 70px;" class="container">
        <div class="row">
            
        
