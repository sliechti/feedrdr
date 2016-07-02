<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.config.FeedAppConfig"%>

<%! static String baseUrl = FeedAppConfig.BASE_APP_URL; %>
<%
	UserData user = (UserData)request.getAttribute("user");
%>

<script id="header_right_tools" type="text/x-handlebars-template">
    <div id="right_tools">
    <div class="box right">
        <a title="show as list" href="" onclick="setLineView();return false;"><span id="icon_view_line" class="left" title="line view"></span></a>
        <a title="show in magazine view" href="" onclick="setMagazineView();return false;"><span id="icon_view_mag" class="left" title="magazine view"></span></a>
        <a title="show in stream view" href="" onclick="setStreamView();return false;"><span id="icon_view_stream" class="left" title="stream view"></span></a>
        <% //<a href=""><span id="icon_color_picker" class="left" title="color picker"></span></a> %>
    </div>
    {{#if title}}
    {{else}}
    <div class="right">
        <a title="show all" href="" id="show_all" onclick="showAll();return false;">All
        <span class=" glyphicon glyphicon-eye-open"></span></a> |
        <a title="show only unread" href="" id="show_unread" onclick="showUnreadOnly();return false;">Unread (<span id="unread"></span>)
        <span class=" glyphicon glyphicon-eye-close"></span></a>
    </div>    
    {{/if}}
    {{#if title}}
    {{else}}
    <div class="right">
        <a title="show newest first" href="" id="newset_first" onclick="streamSort(SORT_NEW_FIRST);return false;">
        <span class="glyphicon glyphicon-sort-by-order"></span></a> | 
        <a title="show oldest first" href="" id="oldest_first" onclick="streamSort(SORT_OLD_FIRST);return false;">
        <span class="glyphicon glyphicon-sort-by-order-alt"></span></a>
    </div>    
    {{/if}}
    <div class="right">
        <a title="reload stream" href="" class="reload_stream" onclick="reloadStream(); return false;">
        <span class="glyphicon glyphicon-refresh"></span></a>
    </div>
    {{#if title}}
    {{else}}
    <div class="right">
        <a title="mark all entries as read" href="" class="mark_all_read" onclick="markAllRead(); return false;">
        <span class="glyphicon glyphicon-ok-sign"></span></a>
    </div>    
    {{/if}}            
    </div>
</script>

<script id="stream_group_header_tmpl" type="text/x-handlebars-template">
    <div id="div_stream_rename" style="float: left; display: none;">
        <input type="text" id="txt_stream_rename" value="{{s_stream_name}}">
        <button class="btn btn-primary btn-xs" onClick="renameStreamGroup()">save</button>&nbsp;
    </div>
    <span id="span_stream_rename" style="float: left">
    <img class="icon" src="<%= baseUrl%>/img/icon-stream.png">{{s_stream_name}}&nbsp;</span>
    <span id="edit_tools"> | 
		<% if (user.isAdmin()) { %>
			<a title="share collection" href="" onclick="showShareCollection({{l_stream_id}});return false;"><span class="glyphicon glyphicon-share"></span></a> |
		<% } %>
		<a title="rename stream" href="" onclick="showRename('stream_rename');return false;"><span class="glyphicon glyphicon-edit"></span></a> | 
        <a title="add single feed" href="" onclick="showImport(); return false;"><span class="glyphicon glyphicon-plus"></span></a> | 
        <a title="show subscriptions" href="" onClick="toggleHeaderTool('subscriptions');return false;"><span class="glyphicon glyphicon-list-alt"></span></a> |
        <a title="delete stream" href="" onClick="showDeleteStream();return false;"><span class="glyphicon glyphicon glyphicon-remove-sign"></span></a>  |
    </span>
    {{> header_right_tools this}}
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
            <b>Total entries : {{i_total_entries}}, visible to your profile (<%= user.getUserType() %>) : {{count}}</b>
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
    <img class="icon" src="<%= baseUrl%>/img/icon-stream.png">{{title}}&nbsp;{{page}}</span>
{{> header_right_tools}}
</script>

<script id="recently_read_header_tmpl" type="text/x-handlebars-template">
<span style="float: left">
	<img class="icon" src="<%= baseUrl%>/img/icon-stream.png">{{title}}&nbsp;
    <a title="clear all" href="" onClick="confirmClearRecentlyRead();return false;"><span class="glyphicon glyphicon glyphicon-remove-sign"></span></a>
</span>

{{> header_right_tools}}
</script>

<script id="news_line_tmpl" type="text/x-handlebars-template">
    {{#each entries}}
        <div id="news_{{l_entry_id}}" class="news row news_line" data-pos="{{position}}" data-id="{{l_entry_id}}">
            {{#showSourceData l_xml_id ../tmplOptions true}}{{/showSourceData}}
            <div id="title" class="col-xs-{{../midSize}}">
            {{timediff t_pub_date}}
            <a name="link" data-id="{{l_entry_id}}" href="{{s_link}}" target="_blank">
                {{../tmplOptions.titleLen}} {{s_title}}</a>
            </div>
            <div class="col-xs-1 text-right">
            {{#tools ../tmpl l_entry_id}}{{/tools}}
            </div>
        </div>                
    {{/each}}
</script>


<script id="news_mag_tmpl" type="text/x-handlebars-template">
    {{#each entries}}
        <div id="news_{{l_entry_id}}" class="news row news_mag" data-pos="{{position}}" data-id="{{l_entry_id}}">
            <div>
                <img class="left margin10" width="220" height="120" id="img_{{l_entry_id}}" src="<%= baseUrl %>/img/1px.png">
                <a class="title" name="link" data-id="{{l_entry_id}}" href="{{s_link}}" target="_blank">{{s_title}}</a>
                <br>
                <p class="content" id="cnt_{{l_entry_id}}">{{content}}</p>
            </div>            
            <div style="clear: both">
                <div class="left">
                {{#showSourceData l_xml_id ../tmplOptions false}}{{/showSourceData}}
                </div>
                <div class="right">
                    {{timediff t_pub_date}}
                    {{#tools ../tmpl l_entry_id}}{{/tools}}&nbsp;
                </div>
            </div>
        </div>
    {{/each}}
</script>

<script id="news_stream_tmpl" type="text/x-handlebars-template">
    {{#each entries}}
        <div id="news_{{l_entry_id}}" class="news row news_stream" data-pos="{{position}}" data-id="{{l_entry_id}}">
            <div class="img" id="img_{{l_entry_id}}"></div>
            <div>
                <a name="link" class="title" data-id="{{l_entry_id}}" href="{{s_link}}" target="_blank">
                {{../tmplOptions.titleLen}} {{s_title}}</a>
            </div>
            <div class="content" id="cnt_{{l_entry_id}}">{{content}}</div>
            <div>
                <div class="left">
                   {{#showSourceData l_xml_id ../tmplOptions false}}{{/showSourceData}}
                </div>
                <div class="right">
                    <span id="date">{{timediff t_pub_date}}
                    {{#tools ../tmpl l_entry_id}}{{/tools}}&nbsp;</span>
                </div>
            </div>
        </div>
    {{/each}}
</script>

<script id="all_subscriptions_tmpl" type="text/x-handlebars-template">
    <ul class="sub_list">
   {{#each subscriptions}}
    <li id="subscription_l_{{l_subs_id}}" class="subscription_line" 
        onmouseover="$(this).children('.right').show()"  onmouseout="$(this).children('.right').hide()">
        <a onClick="addSubscription({{l_subs_id}});return false;" 
            href="">{{cut s_subs_name 30}}</a>    
        <a class="noshow right" target="_blank" href="#/s/{{l_xml_id}}">source</a>
        <a class="noshow right" target="_blank" href="<%= baseUrl %>/pages/subscriptions.jsp#/v/{{l_subs_id}}">manage</a>
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
        <a class="noshow right" target="_blank" href="<%= baseUrl %>/pages/subscriptions.jsp#/v/{{l_subs_id}}">manage</a>
        <a class="noshow right" href="" onClick="removeSubscription({{l_subs_id}});return false;">remove</a>
    </li>
   {{/each}}
    </ul>
</script>


<script id="stream_groups_tmpl" type="text/x-handlebars-template">
    <ul>
    {{#each groups}}
         <li id="e_{{l_stream_id}}">{{@key}}<a href="#/f/{{l_stream_id}}">{{cut s_stream_name 17}}</a>
         <label id="e_c_{{l_stream_id}}">{{showGroupCount this}}</label></li>
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
    <center><h3>Let's start adding some content</h3>
        <p>You may start by <a href="import.jsp">importing your feeds</a> or<br>
	by adding one of the <a href="collections.jsp">collections</a> created by us or <br>
	by creating a new <a href="" onclick="newStreamGroup(); return false;">stream group</a>
	and adding single subscriptions.</center></p>
<br><br></center></p>
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
    <p class="lead"><a href="<%= baseUrl %>"/reader.jsp>Go to start</a></p></center>
</script>

<script id="stream_group_start_tmpl" type="text/x-handlebars-template">
    <center><h3>No stream groups yet.</h3>
	{{> create_new_stream_group}}
</script>


<div id="div_delete_stream" data-stream_id="0" style="display: none">
    <div class="modal-body">
        <input type="checkbox" name="delete_profiles" checked="false">&nbsp;
        <label for="delete_profiles" onclick="toggleCheckbox('input[name=delete_profiles]')">Remove stream group from all profiles.</label><br>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default" onclick="$('#modalBox').hide()" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-danger" onclick="deleteStream()">Delete Stream</button>
    </div>
</div>

<div id="div_add_subscription" data-stream_id="0" style="display: none; visibility: hidden">
    <div class="modal-body">
        <label for="feed_name">Subscription's name</label>
        <input class="form-control" type="text" name="sName"><br>
        <label for="feed_url">URL&nbsp;</label>
        <input class="form-control" type="text"  name="sUrl" placeholder="http://domain.com/feed"><br>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary" onclick="importSingleFeed()">Add Subscription</button>
    </div>
</div>

<% if (user.isAdmin()){  %>
<div id="div_share_collection" data-stream_id="0" style="display: none; visibility: hidden">
    <div class="modal-body">
        <label for="feed_name">
        Name of collection</label>
        <input class="form-control" type="text" id="collectionName" name="name"><br>
        <label for="feed_url">Description</label>
        <textarea class="form-control" id="collectionDesc"  name="description"></textarea><br>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary" onclick="shareCollection()">Share collection</button>
    </div>
</div>
<% } %>

