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
</jsp:include>

<div id="stream-header" class="sub-header sub-line">
	<div class="content center content-width">
	</div>
</div>

<div class="center content-width">
<div id="stream-content">
	<div id="stream-entries"></div>
	<jsp:include page="stream-footer.jsp" />
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
