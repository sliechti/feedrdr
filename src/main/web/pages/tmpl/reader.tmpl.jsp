<script id="stream-options" type="text/x-handlebars-template">
	{{log 'stream-options' this}}
	<div id="stream-options" class="el-menu el-menu-header">
		<ul>
		{{#if options.showFilter}}
			<li>
			<a href="" id="show_all" onclick="showAll();return false;">
				All
				<i class="fa fa-eye"></i>
			</a>
			</li>
			<li>
			<a href="" id="show_unread" onclick="showUnreadOnly();return false;">
				Unread
				&nbsp;(<span id="unread"></span>)
				<i class="fa fa-eye-slash"></i>
			</a>
			</li>
		{{/if}}
		{{#if options.showRanking}}
			<li>
			<a href="" onclick="streamSort(SORT_NEW_FIRST);return false;">
				New first
				<i class="fa fa-sort-numeric-asc"></i>
			</a>
			</li>
			<li>
			<a href="" onclick="streamSort(SORT_OLD_FIRST);return false;">
				Oldest first
				<i class="fa fa-sort-numeric-desc"></i>
			</a>
			</li>
		{{/if}}
			<li>
			<a href="" onclick="setLineView();return false;">
				Line
				<span class="icon" id="icon_view_line" title="line view"></span>
			</a>
			</li>
			<li>
			<a href="" onclick="setMagazineView();return false;">
				Magazine
				<span class="icon" id="icon_view_mag" title="magazine view"></span>
			</a>
			</li>
			<li>
			<a href="" onclick="setStreamView();return false;">
				Stream
				<span class="icon" id="icon_view_stream" title="stream view"></span>
			</a>
			</li>
		</ul>
	</div>
</script>

<script id="stream-header-tmpl" type="text/x-handlebars-template">
	{{log 'stream-header-tmpl' this}}
	<div class="stream-header">
		<div class="angle" onclick="shToggleActions(this)">
			<i class="pointer fa fa-angle-right"></i>
		</div>
		<div class="stream-title title">
			{{stream.s_stream_name}}
			<div id="stream-actions" class="hide">
				{{#if options.showMarkAllRead}}
				<a href="" onclick="markAllRead(); return false;">
					<i class="fa fa-check" title="Mark stream read"></i>
				</a>
				{{/if}}
				{{#if options.showAdd}}
				<a href="${baseUrl}/add?to={{stream.l_stream_id}}">
					<i class="fa fa-plus" title="Add content"></i>
				</a>
				{{/if}}
				{{#if options.showSubscriptions}}
				<a href="${baseUrl}/add?to={{stream.l_stream_id}}">
				<a href="" onclick="toggleSubscriptions()">
					<i class="fa fa-list" title="List subscriptions"></i>
				</a>
				{{/if}}
				{{#if options.showClearRecentlyRead}}
				<a href="" onclick="confirmClearRecentlyRead(); return false;">
					<i class="fa fa-remove" title="Clear recently read"></i>
				</a>
				{{/if}}
				{{#if options.showClearSaved}}
				<a href="" onclick="clearSaved(); return false;">
					<i class="fa fa-remove" title="Clear saved list"></i>
				</a>
				{{/if}}
				{{#if options.showDeleteStream}}
				<a href="" onclick="deleteStream({{stream.l_stream_id}}); return false;">
					<i class="fa fa-remove" title="Delete stream"></i>
				</a>
				{{/if}}
			</div>
		</div>
		<div class="right relative">
			<i onclick="showEl(this, 'stream-options')"
				class="-icon-hide pointer fa fa-ellipsis-v right fade-color"></i>
			{{> stream-options}}
		</div>
	</div>
</script>


<script id="source-header-tmpl" type="text/x-handlebars-template">
{{log 'source header tmpl' this}}
	<div>
		<a href="" onclick="location.history(-1); return false">Back</a>
	</div>
<div class="no-overflow">
	{{#if s_img_url}}
	<div class="source-logo">
		<img src="{{s_img_url}}">
	</div>
	{{/if}}
	<div class="channel-info">
		<img src="{{favico s_xml_url}}">
		<a href="{{s_link}}" target="_blank">{{s_title}}</a>
		<br>
		<!-- <span class="subscibe">
			<a href="#" class="source-add">
				<i class="fa fa-plus-circle"></i>
				subscribe
			</a>
		</span> -->
	</div>
</div>
<div class="source-info">
	<div class="channel-desc">{{s_description}}</div>
	<div class="channel-entries w100p text-right">
		entries {{i_total_entries}} -
		<a href="{{s_xml_url}}" target="_blank">feed</a>
		last updated on: {{formatUnixTs t_checked_at}}
		<div class="right">
			&nbsp;
			<i onclick="shToggleSettings()"
				class="-icon-hide pointer fa fa-ellipsis-v right fade-color"></i>
		</div>
	</div>
</div>
{{> stream-options}}
</script>


<script id="simple_view_header_tmpl" type="text/x-handlebars-template">
<span style="float: left">
		<img class="icon" src="${baseUrl}/img/icon-stream.png">{{title}}&nbsp;{{page}}</span>
{{> stream-options}}
</script>

<script id="recently_read_header_tmpl" type="text/x-handlebars-template">
<span style="float: left">
	<img class="icon" src="${baseUrl}/img/icon-stream.png">{{title}}&nbsp;
		<a title="clear all" href="" onClick="confirmClearRecentlyRead();return false;"><span class="glyphicon glyphicon glyphicon-remove-sign"></span></a>
</span>
{{> stream-options}}
</script>

<script id="news_line_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
	<div id="line-view-options-tmpl" class="options hide">
	<a href="#" onclick="slForwardSaveEntry(this);return false;">
		<i class="fa fa-bookmark-o"></i>
	</a>
	</div>
		{{#each entries}}
		<div id="article-{{l_entry_id}}" class="article" >
			<div class="angle" onclick="slLineOptions(this, '{{l_entry_id}}')">
				<i class="pointer fa fa-angle-right fade-color"></i>
			</div>
			<div class="content">
				{{timediff t_pub_date}}
				<a name="link" href="{{s_link}}" data-id="{{l_entry_id}}" target="_blank">{{s_title}}</a>
			</div>
		</div>
		{{/each}}
</div>
</script>


<script id="news_mag_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
	{{#each entries}}
	<div id="news_${l_entry_id}" class="news-mag">
		<div class="content">
			<img style=""
			class="left margin10" id="img_{{l_entry_id}}" src="${baseUrl}/img/1px.png">
			<a name="link" class="title" href="{{s_link}}" data-id="{{l_entry_id}}" target="_blank">
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
				<a href="#" onclick="saveEntry(this, {{l_entry_id}});return false;">
					<i class="fa fa-bookmark-o"></i>
				</a>
			</div>
			<div class="right source">
				<a href="#">
					<a href="#/s/{{l_xml_id}}">
						{{#sourceName l_xml_id}}{{/sourceName}}
					</a>
				</a>
			</div>
		</div>
	</div>
	{{/each}}
</div>
</script>

<script id="news_stream_tmpl" type="text/x-handlebars-template">
<div id="stream-list" class="line-view">
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
				<a href="#" onclick="saveEntry(this, {{l_entry_id}});return false;">
					<i class="fa fa-bookmark-o"></i>
				</a>
			</div>
			<div class="right source">
				<a href="#/s/{{l_xml_id}}">
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
		<center>
			<h3>
				No entries found for this source yet.
				Check the <a href="subscriptions.jsp#/v/{{id}}">status
			</h3>
		</center>
</script>

<script id="content_start_source" type="text/x-handlebars-template">
		<div class="text-center">
			<h3>New Stream</h3>
			<p>
				Let's add some content
			</p>
			<div onclick="addContent()">
				<div class="box" onclick="addContent()">Add content</div>
			</div>
		</div>
</script>

<script id="content_start_all" type="text/x-handlebars-template">
			<h3>Let's start adding some content</h3>
		<p class="important-a">
			The easiest way to subscribe to feeds is adding one of the many <a href="${baseUrl}/collections">collections</a>
we created for you.<br>
			<br>
			You can <a href="${baseUrl}/add">import</a> your own feeds with an OPML file.
			<br>
		</p>
</script>

<script id="content_start_recently_read" type="text/x-handlebars-template">
		<div class="center text-center">
			<b>List empty</b>
			<p>
			Articles you read are automatically added to this list.<br><br>
			You can clear the list by clicking on the '>' icon and then 'x'.
			</p>
		</div>
</script>

<script id="content_start_saved" type="text/x-handlebars-template">
		<center><h3>No content saved yet. </h3>
				<p class>You can save content to read later, just press on the save button
		<span class="glyphicon glyphicon-floppy-disk"></span>, all content will start appearing here. </center></p>
</script>

<script id="content_all_read" type="text/x-handlebars-template">
		<center><h3>Finished reading stream group {{name}}.</h3>
		<p class="lead">{{#showNextOptions}}{{/showNextOptions}}</p>
		<div class="box" onclick="addContent()">
		Add content
		</div>
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
