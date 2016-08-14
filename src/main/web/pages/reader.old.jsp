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

