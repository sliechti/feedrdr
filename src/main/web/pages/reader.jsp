<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

<jsp:include page="header.jsp">
	<jsp:param name="title" value="reader" />
	<jsp:param name="baseUrl" value="${baseUrl}" />
</jsp:include>

<div id="stream_content">

    <div id="simple_view_header" class="row"></div>

    <div id="stream_group_view_header" class="row">

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

<jsp:include page="tmpl/reader.tmpl.jsp" />

<script type="text/javascript">
	setBaseUrl('${baseUrl}');
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
        console.debug(route);
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

<jsp:include page="footer.jsp" />
