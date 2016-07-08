<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.security.Admin"%>
<%@page import="feedreader.time.DateUtils"%>
<%@page import="feedreader.store.FeedSourcesTable"%>
<%@page import="feedreader.entities.FeedSourceEntry"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="feedreader.entities.UserFeedSubscription"%>
<%@page import="feedreader.utils.StringUtils"%>
<%@page import="feedreader.utils.LinkUtils"%>
<%@page import="java.util.Date"%>
<%@page import="feedreader.store.FeedEntriesTable"%>
<%@page import="feedreader.entities.FeedEntry"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.TreeSet"%>
<%@page import="feedreader.config.FeedAppConfig"%>

<% request.setAttribute("j", true); %>
<% request.setAttribute("s", true); %>
<% request.setAttribute("r", true); %>
<% request.setAttribute("rs", true); %>

<%@include file="header.jsp" %>

<div id="leftbar" style="max-width: 200px; position: fixed; top: 70px;" class="hidden-xs col-sm-3 col-md-2">
    <div class="right">
        <a title="close menu" href="" onclick="closeMenu(); return false;"><span class="glyphicon glyphicon-remove-circle"></span></a>
    </div>
    <div>
        <a href="#/v/a" id="mAll" style="display: block">All</a>
        <a href="#/v/s" id="mSaved" style="display: block">Saved</a>
        <a href="#/v/r" id="mRr" style="display: block">Recently read</a>
        <% if (user.isAdmin()) { %>
        	<a class="admin" href="<%= FeedAppConfig.BASE_APP_URL %>/beta/api.jsp" style="display: block">API</a>
        	<a class="admin" href="<%= FeedAppConfig.BASE_APP_URL %>/jobs/run_fetch.jsp" style="display: block">Fetch</a>
        	<a class="admin" href="<%= FeedAppConfig.BASE_ADMIN_URL %>" style="display: block">Admin</a>
        <% } %>
    </div>
    <br>
    <div>
    	<a href="<%= PageUtils.getPath("/pages/import.jsp") %>" style="display: block">Import</a>
    	<a href="<%= PageUtils.getPath("/pages/subscriptions.jsp") %>" style="display: block">Subscriptions</a>
    </div>
    <br>
    <div>
        <a title="show all" href="" onclick="showOnlyWithUnread(false); return false;"><span class="glyphicon glyphicon-eye-open"></span></a> |
        <a title="show only unread" href="" onclick="showOnlyWithUnread(true); return false;"><span class="glyphicon glyphicon-eye-close"></span></a><br>
        <a title="sort A-Z" href="" onclick="sortByAlphabet(2);return false;"><span class="glyphicon glyphicon-sort-by-alphabet"></span></a> |
        <a title="sort Z-A" href="" onclick="sortByAlphabet(1);return false;"><span class="glyphicon glyphicon-sort-by-alphabet-alt"></span></a> |
        <a title="sort by unread 9-0" href="" onclick="sortByUnread(1);return false;"><span class=" glyphicon glyphicon-sort-by-attributes-alt"></span></a> |
        <a title="sort by unread 0-9" href="" onclick="sortByUnread(2);return false;"><span class="glyphicon glyphicon-sort-by-attributes"></span></a>
        <a title="refresh" href="" onclick="refreshUnread(2);return false;"><span class="glyphicon glyphicon-refresh"></span></a>
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

    <div id='menusubs' onmouseover="$('#menusubs').css('overflow-y', 'auto');"
         onmouseout="$('#menusubs').css('overflow-y', 'hidden');"></div>
</div>

<div id="stream_content" class="col-xs-12 col-sm-offset-3 col-md-offset-2 col-sm-9 col-md-10">

    <div id="simple_view_header" class="row" onmouseover="$('#edit_tools').show()"
     onmouseout="$('#edit_tools').hide()"></div>

    <div id="stream_group_view_header" class="row" onmouseover="$('#edit_tools').show()"
         onmouseout="$('#edit_tools').hide()">

        <div id="stream_group_header" class="row"></div>

        <div id="header_subscriptions" class="noshow row">
	        <div class="row">
	        	<div class="col-md-12">
	        		<a href="" onclick="toggleHeaderTool('subscriptions');return false;">
	        			<span class="right glyphicon glyphicon-remove-circle"></span>
	       			</a>
	        	</div>
			</div>
            <div class="row">
                <div class="col-md-6">
                    <h4><label id="all_count"></label> subscriptions: [ <a href="<%= PageUtils.getPath("/pages/import.jsp") %>">import</a> /
                    <a href="<%= PageUtils.getPath("/pages/subscriptions.jsp") %>">manage</a> ]</h4>
                    <input class="form-control" type="text" id="searchQueryAll"  tabindex="5" placeholder="search" onchange="filterByKeywordAll()"><br>
                    <div id="all_subscriptions" tabindex="6" class="subsdata"></div>
                </div>
                <div class="col-md-6">
                    <h4><label id="stream_count"></label> subscriptions in this group: </h4>
                    <input class="form-control" type="text" id="searchQuerySubs" tabindex="7" placeholder="search" onchange="filterByKeywordSubs()"><br>
                    <div id="subscribed" tabindex="8" class="subsdata row"></div>
                </div>
            </div>
            <div id="reload" class="noshow theme-bgcolor text-center">
                <a href="" onclick="reloadSelected();return false;" class="lead block">Save and reload</a>
            </div>
        </div>

    </div>

    <div id="stream"  class="row">
        <div id="stream_entries" class="row"></div>
        <div id="stream_more" class="noshow row block text-center lead">
            <a href="" class="footer_links" onclick="loadMore(); return false;">load more</a>
        </div>
        <div class="mark_all_read noshow">
            <a href="" class="footer_links" onclick="markAllRead(); return false;">mark all read</a>
        	<div id="footer_spacing" style="height: 600px"></div>
        </div>

    </div>


</div>

<div style="position: fixed; left: 0px; width: 100%; bottom: 0px">
    <div id="loader" class="profileColor" style="height: 1px; "></div>
    <div class="container">
        <div class="row" style="font-size: 18px; height: 30px; text-align: center; background-color: white" >
            <div>
                <label id="footer_unread"></label> |
                <a href="" class="reload_stream" onclick="reloadStream(); return false;">reload stream</a> |
                <a href="" class="mark_all_read" onclick="markAllRead(); return false;">mark all read</a> |
                <a href="" onclick="window.scrollTo(0,0);return false;">top <span class="glyphicon glyphicon-chevron-up"></span></a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="tmpl/reader.tmpl.jsp"></jsp:include>

<script type="text/javascript">
    window.scrollTo(0,0);
    $("#mark_all_read").css("margin-top", screen.height - (screen.height *0.3));

    initReader();
    setEntriesPerPage(<%= FeedAppConfig.DEFAULT_API_FETCH_ARTICLES %>);

    registerOnProfileSelected(function(data) {
        clearContent();
        moveTop();
        setProfile(selectedProfile);

        registerChangeViewListener(function() {
        	$("a[name=link]").on("click", function(e) {
        		var queryData = {};
        		queryData.e = 0;
        		queryData.id = $(e.target).attr("data-id");
        		$.getJSON(baseUrl + apiUrlEntriesUpdate, queryData, function() { /**/ });
        		updateUnread($("#unread").text() - 1);
        	});
        });

        var route = router.getRoute();
        console.log("route " + route.length);
        if(route && route.length == 1) {
            getStreamGroups(function(streamGroups, filteredStreamGroups) {
                if (filteredStreamGroups.length > 0) {
                    return loadStream(filteredStreamGroups[0].l_stream_id);
                } else if (streamGroups.length > 0) {
                    return loadStream(streamGroups[0].l_stream_id);
                } else {
                	renderContentStart();
                }
            });
            return;
        }

		switch (route[0]) {
			case ROUTE_FEED:
				getStreamGroups(function() {
					loadStream(route[1]);
				});
				break;
			case ROUTE_FEED_SOURCE:
				getStreamGroups(function() {
					loadSource(route[1]);
				});
				break;
			case ROUTE_VIEW:
				switch (route[1]) {
				case ROUTE_ALL:
					getStreamGroups(function() {
						loadAll(route[2]);
					});
					break
				case ROUTE_SAVED:
					getStreamGroups(function() {
						loadSaved(route[2]);
					});
					break
				case ROUTE_RECENTLY_READ:
					getStreamGroups(function() {
						loadRecentlyRead(route[2]);
					});
					break
				default:
				}
				break;

			default:
				loadUnknown();
			}
		});
</script>

<jsp:include page="footer.jsp"></jsp:include>
