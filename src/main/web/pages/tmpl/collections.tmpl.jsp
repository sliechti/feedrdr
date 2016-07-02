<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.entities.UserData"%>

<% 
    UserData user = (UserData)request.getAttribute("user");
%>

<script type="x-handlebars" id="collections_tmpl">
<div style="padding: 5px; margin: 5px; min-width: 220px;">
	<h3 style="background-color: #CACACA;">{{c.s_name}}
		<a href="" onClick="showAddModal(this, {{c.l_collection_id}}); return false;">
		<span class="right glyphicon glyphicon-plus"></span></a></h3>
	<p>{{c.s_description}}</p>
	<p>feeds ({{c.i_feeds}}) <a href="" onClick="toggleFeedsCollection(this, {{c.l_collection_id}}); return false;"> ... show </a></p>
	<div class="noshow border" id="feeds_{{c.l_collection_id}}"></div>
</div>
</script>

<script type="x-handlebars" id="feeds_tmpl">
<ul style="padding: 5px; display: block;">
	{{#each entries}}
	<li class="clearli hoverli"><img src="{{favico}}{{s_link}}">&nbsp;<a href="{{s_link}}" target="_blank">{{cut s_feed_name 30}}</a></li>
	{{/each}}
</ul>
</script>

<script type="x-handlebars" id="collection_created_tmpl">
The collection was added to you list of streams, <a href="reader.jsp?#/f/{{streamId}}">see in reader</a> or 
<a href="" onClick="hideModal();return false;">keep adding collections</a>
</script>

<div id="modal_collection_add" style="display: none; visibility: hidden">
    <div class="modal-body">
    	<div id="collectionCreated" class="noshow"></div>
        <input type="hidden" id="collectionId" value="0"><br>
        <label for="collection_name">
        Name of collection</label>
        <input class="form-control" type="text" id="collectionName"><br>
        <select id="selectedProfiles" multiple="multiple" style="width: 100%">
            <% for (ProfileData p : user.getProfileData()) { %>
            <option value="<%= p.getProfileId() %>"><%= p.getName() %></option>
            <% } %>
        </select>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default" onclick="hideModal()" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary" onclick="importCollection()">Import Collection</button>
    </div>
</div>