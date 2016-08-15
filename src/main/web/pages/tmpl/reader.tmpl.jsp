<script id="header_right_tools" type="text/x-handlebars-template">
		<div id="view-options" class="right">
			<a class="pointer" onclick="$('.right_tools').toggle()">Options</a>
		</div>
		<div id="right_tools" class="right_tools" style="display: none">

		{{#if title}}
		{{else}}
				<a title="show newest first" href="" id="newset_first" onclick="streamSort(SORT_NEW_FIRST);return false;">
				<span class="glyphicon glyphicon-sort-by-order"></span></a>
				<a title="show oldest first" href="" id="oldest_first" onclick="streamSort(SORT_OLD_FIRST);return false;">
				<span class="glyphicon glyphicon-sort-by-order-alt"></span></a>
		{{/if}}
				<a title="reload stream" href="" class="reload_stream" onclick="reloadStream(); return false;">
				<span class="glyphicon glyphicon-refresh"></span></a>
		{{#if title}}
		{{else}}
				<a title="mark all entries as read" href="" class="mark_all_read" onclick="markAllRead(); return false;">
				<span class="glyphicon glyphicon-ok-sign"></span></a>
		{{/if}}
		</div>
</script>

<script id="stream-header-tmpl" type="text/x-handlebars-template">
	{{log 'stream-header-tmpl' this}}
	<div class="stream-header">
		<div class="angle" onclick="shToggleActions(this)">
			<i class="pointer fa fa-angle-right"></i>
		</div>
		<div class="stream-title title">
			{{s_stream_name}}
			<div id="stream-actions" class="hide">
				<a href="" onclick="markAllRead(); return false;">
					<i class="fa fa-check" title="Mark stream read"></i>
				</a>
				<i class="fa fa-plus" title="Add content"></i>
				<a href="" onclick="deleteStream(); return false;">
					<i class="fa fa-remove" title="Delete stream"></i>
				</a>
			</div>
			<div class="right">
				<i onclick="shToggleSettings()"
					class="-icon-hide pointer fa fa-cog right fade-color"></i>
			</div>
		</div>
	</div>
	<div id="stream-options" class="hide">
		<div class="filter-box">
		{{#if title}}
		{{else}}
			<a title="show all" href="" id="show_all" onclick="showAll();return false;">
				<i class="fa fa-eye"></i>
			</a>
			<a title="show only unread" href="" id="show_unread" onclick="showUnreadOnly();return false;">
				<i class="fa fa-eye-slash"></i>
				&nbsp;(<span id="unread"></span>)
			</a>
		{{/if}}
		</div>
		<div class="ranking-box">
			<a href="" onclick="streamSort(SORT_NEW_FIRST);return false;">
				<i class="fa fa-sort-numeric-asc"></i>
			</a>
			<a href="" onclick="streamSort(SORT_OLD_FIRST);return false;">
				<i class="fa fa-sort-numeric-desc"></i>
			</a>
		</div>
		<div class="views-box">
			<a title="show as list" href="" onclick="setLineView();return false;">
				<span class="icon" id="icon_view_line" title="line view"></span>
			</a>
			<a title="show in magazine view" href="" onclick="setMagazineView();return false;">
				<span class="icon" id="icon_view_mag" title="magazine view"></span>
			</a>
			<a title="show in stream view" href="" onclick="setStreamView();return false;">
				<span class="icon" id="icon_view_stream" title="stream view"></span>
			</a>
		</div>
	</div>
</script>


<script id="source_header_tmpl" type="text/x-handlebars-template">
		<div class="row">
				{{#if s_img_url}}
				<div class="margin10 col-xs-2">
						<img id="source_image" src="{{s_img_url}}">
				</div>
				{{/if}}
				<div class="margin10">
						<span id="stream_name">
						<img src="{{favico s_xml_url}}">
						<a href="{{s_link}}" target="_blank">{{toUpperCase s_title}}</a>
						|| <a href="{{s_xml_url}}" target="_blank">XML FEED</a><br>
						<b>Total entries : {{i_total_entries}}, visible to your profile (${user.userType}) : {{count}}</b>
						</span>
						<br><br>
						<span id="source_description">{{s_description}}</span><br>
						<div id="user_subscription" class="noshow">
								<div id="div_subscription_rename" style="float: left; display: none;">
									 <input type="text" id="txt_subscription_rename" value="">
									 <button class="btn btn-primary btn-xs" onClick="renameSubscription()">save</button>&nbsp;
								</div>
								<a href="" id="span_subscription_rename">subscription_name</a>
								<span class="glyphicon glyphicon-edit pointer" onclick="showRename('subscription_rename');return false;"></span>
								<br>
								Last updated on: {{formatUnixTs t_checked_at}}
						</div>
				</div>
				{{> header_right_tools}}
		</div>
</script>


<script id="simple_view_header_tmpl" type="text/x-handlebars-template">
<span style="float: left">
		<img class="icon" src="${baseUrl}/img/icon-stream.png">{{title}}&nbsp;{{page}}</span>
{{> header_right_tools}}
</script>

<script id="recently_read_header_tmpl" type="text/x-handlebars-template">
<span style="float: left">
	<img class="icon" src="${baseUrl}/img/icon-stream.png">{{title}}&nbsp;
		<a title="clear all" href="" onClick="confirmClearRecentlyRead();return false;"><span class="glyphicon glyphicon glyphicon-remove-sign"></span></a>
</span>

{{> header_right_tools}}
</script>

<script id="news_line_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
	<div id="line-view-options-tmpl" class="options hide">
		<i class="fa fa-bookmark-o"></i>
		<i class="fa fa-share"></i>
		<i class="fa fa-tag"></i>
		<i class="fa fa-info-circle"></i>
	</div>
		{{#each entries}}
		<div id="article-{{l_entry_id}}" class="article" >
			<div class="angle" onclick="slLineOptions(this, '{{l_entry_id}}')">
				<i class="pointer fa fa-angle-right fade-color"></i>
			</div>
			<div class="content">
				{{timediff t_pub_date}}
				<a href="{{s_link}}">{{s_title}}</a>
			</div>
		</div>
		{{/each}}
</div>
</script>


<script id="news_mag_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
	<div id="line-view-options-tmpl" class="options hide">
		<i class="fa fa-bookmark-o"></i>
		<i class="fa fa-share"></i>
		<i class="fa fa-tag"></i>
		<i class="fa fa-info-circle"></i>
	</div>
		{{#each entries}}
		<div id="news_${l_entry_id}" class="news-mag">
			<div class="content">
				<img style="background: transparent url(&quot;URL;) no-repeat scroll center center"
				class="left margin10" id="img_{{l_entry_id}}" src="/img/1px.png">
				<a class="title" href="{{s_link}}">
					{{timediff t_pub_date}}
					{{s_title}}
				</a>
				<div>
					<p id="cnt_{{l_entry_id}}">
					{{content}}
					</p>
				</div>
			</div>
			<div class="news-footer">
				<div class="actions">
					<i class="fa fa-bookmark-o"></i>
					<i class="fa fa-share"></i>
					<i class="fa fa-tag"></i>
					<i class="fa fa-info-circle"></i>
				</div>
				<div class="right source">
					<a href="#">
						mashable
					</a>
				</div>
			</div>
		</div>
		{{/each}}
</div>
</script>

<script id="news_stream_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
	<div id="line-view-options-tmpl" class="options hide">
		<i class="fa fa-bookmark-o"></i>
		<i class="fa fa-share"></i>
		<i class="fa fa-tag"></i>
		<i class="fa fa-info-circle"></i>
	</div>
		{{#each entries}}
		<div id="news_{{l_entry_id}}" class="news-card">
			<div class="img-wrap" id="img_{{l_entry_id}}">
				<img src="">
			</div>
			<div class="content">
				<a name="link" class="title" data-id="{{l_entry_id}}" href="{{s_link}}" target="_blank">
					{{timediff t_pub_date}}
					{{s_title}}
				</a>
				<p class="content" id="cnt_{{l_entry_id}}"></p>
			</div>
			<div class="news-footer">
				<div class="actions">
					<i class="fa fa-bookmark-o"></i>
					<i class="fa fa-share"></i>
					<i class="fa fa-tag"></i>
					<i class="fa fa-info-circle"></i>
				</div>
				<div class="right source">
					<a href="#">
						{{#sourceName l_xml_id}}{{/sourceName}}
					</a>
				</div>
			</div>
		</div>
		{{/each}}
</div>
</script>

<script id="all_subscriptions_tmpl" type="text/x-handlebars-template">
		<ul class="sub_list">
	 {{#each subscriptions}}
		<li id="subscription_l_{{l_subs_id}}" class="subscription_line"
				onmouseover="$(this).children('.right').show()"  onmouseout="$(this).children('.right').hide()">
				<a onClick="addSubscription({{l_subs_id}});return false;"
						href="">{{cut s_subs_name 30}}</a>
				<a class="noshow right" target="_blank" href="#/s/{{l_xml_id}}">source</a>
				<a class="noshow right" target="_blank" href="${baseUrl}/pages/subscriptions.jsp#/v/{{l_subs_id}}">manage</a>
				<a class="noshow right" href="" onClick="addSubscription({{l_subs_id}});return false;" >add</a>
		</li>
	 {{/each}}
		</ul>
</script>


<script id="subscribed_tmpl" type="text/x-handlebars-template">
		<ul class="sub_list">
	 {{#each subscriptions}}
		<li id="subscription_r_{{l_subs_id}}" class="subscription_line"
				onmouseover="$(this).children('.right').show()"  onmouseout="$(this).children('.right').hide()">
				<a title="remove subscription" onClick="removeSubscription({{l_subs_id}});return false;"
						href="">{{cut s_subs_name 30}}</a>
				<a class="noshow right" target="_blank" href="#/s/{{l_xml_id}}">source</a>
				<a class="noshow right" target="_blank" href="${baseUrl}/pages/subscriptions.jsp#/v/{{l_subs_id}}">manage</a>
				<a class="noshow right" href="" onClick="removeSubscription({{l_subs_id}});return false;">remove</a>
		</li>
	 {{/each}}
		</ul>
</script>


<script id="stream_groups_tmpl" type="text/x-handlebars-template">
<ul>
	{{#each groups}}
	<li id="e_121">
		<div class="left">
			<a href="#/f/{{l_stream_id}}" onclick="closeLeftBar()">
				{{s_stream_name}}
			</a>
		</div>
		<div id="e_c_121" class="w50px right text-right pr20p">
			{{showGroupCount this}}
		</div>
	</li>
	{{/each}}
</ul>
</script>
<script id="stream_groups_small_tmpl" type="text/x-handlebars-template">
		<ul style="overflow-x: scroll; height: 200px;">
		{{#each groups}}
						<li id="e_{{l_stream_id}}">{{@key}}<a href="#/f/{{l_stream_id}}">{{cut s_stream_name 17}}</a>
						<label id="e_c_{{l_stream_id}}">{{showGroupCount this true}}</label></li>
		{{/each}}
		</ul>
</script>

<script id="content_empty_source" type="text/x-handlebars-template">
		<center><h3>No entries found for this source yet. Check the <a href="subscriptions.jsp#/v/{{id}}">status</h3></center>
</script>

<script id="content_start_source" type="text/x-handlebars-template">
		<center><h3>Stream group has no subscriptions yet.</h3>
				<p>Start by <a onClick="showImport();return false;" href="#">adding a new feed </a> OR
				by selecting one of your <a href="" onclick="toggleHeaderTool('subscriptions');return false;">subscriptions</a>
		<br><br></center></p>
</script>

<script id="content_start_all" type="text/x-handlebars-template">
	<div class="center w60p">
		<p class="lead">
			Let's start adding some content
		</p>
		<p class="important-a">
			The easiest way to subscribe to feeds is adding one of the many <a href="collections.jsp">collections</a>
we created for you.<br>
			<br>
			You can <a href="import.jsp">import</a> your own feeds with an OPML file.
			<br>
			<br>
			You can also create a new stream group, by opening the <a href="#" onclick="openLeftBar();"> left bar</a>
			and adding new sources with the RSS/Atom URL.
		</p>

		</div>
</script>

<script id="content_start_recently_read" type="text/x-handlebars-template">
		<center><h3>No content read yet.</h3>
				<p>Whenever you clik on a news entry it will be added to this view. </center></p>
</script>

<script id="content_start_saved" type="text/x-handlebars-template">
		<center><h3>No content saved yet. </h3>
				<p class>You can save content to read later, just press on the save button
		<span class="glyphicon glyphicon-floppy-disk"></span>, all content will start appearing here. </center></p>
</script>

<script id="content_all_read" type="text/x-handlebars-template">
		<center><h3>Finished reading stream group {{name}}.</h3>
		<p class="lead">{{#showNextOptions}}{{/showNextOptions}}</p>
	<p class="lead">Add new content, search <a href="collections.jsp">collections</p></center>
</script>

<script id="content_unknown" type="text/x-handlebars-template">
		<center><h3>Page not found.</h3>
		<p class="lead"><a href="${baseUrl}/reader">Go to start</a></p></center>
</script>

<script id="stream_group_start_tmpl" type="text/x-handlebars-template">
		<center><h3>No stream groups yet.</h3>
	{{> create_new_stream_group}}
</script>


<div id="div_delete_stream" data-stream_id="0" style="display: none">
	<div class="modal-body">
		<input type="checkbox" name="delete_profiles" checked="false">&nbsp; <label for="delete_profiles"
			onclick="toggleCheckbox('input[name=delete_profiles]')">Remove stream group from all profiles.</label><br>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" onclick="$('#modalBox').hide()" data-dismiss="modal">Close</button>
		<button type="button" class="btn btn-danger" onclick="deleteStream()">Delete Stream</button>
	</div>
</div>

<div id="div_add_subscription" data-stream_id="0" style="display: none; visibility: hidden">
	<div class="modal-body">
		<label for="feed_name">Subscription's name</label> <input class="form-control" type="text" name="sName"><br> <label
			for="feed_url">URL&nbsp;</label> <input class="form-control" type="text" name="sUrl" placeholder="http://domain.com/feed"><br>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
		<button type="button" class="btn btn-primary" onclick="importSingleFeed()">Add Subscription</button>
	</div>
</div>

<c:if test="${user.admin}">
	<div id="div_share_collection" data-stream_id="0" style="display: none; visibility: hidden">
		<div class="modal-body">
			<label for="feed_name"> Name of collection</label> <input class="form-control" type="text" id="collectionName" name="name"><br>
			<label for="feed_url">Description</label>
			<textarea class="form-control" id="collectionDesc" name="description"></textarea>
			<br>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
			<button type="button" class="btn btn-primary" onclick="shareCollection()">Share collection</button>
		</div>
	</div>
</c:if>
