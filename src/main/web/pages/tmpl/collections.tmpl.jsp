<script type="x-handlebars" id="collections_tmpl">
<div class="collection">
	<p class="title">
		{{c.s_name}}
		<a href="${baseUrl}/add?collection={{c.l_collection_id}}">
			<i class="right fa fa-plus"></i>
		</a>
		<a href="" onClick="toggleFeedsCollection(this, {{c.l_collection_id}}); return false;">
			<i class="right fa fa-list"></i>
		</a>
	</p>
	<p class="description">{{c.s_description}}</p>
	<div class="hide feeds-container" id="feeds_{{c.l_collection_id}}">
	</div>
</div>
</script>

<script type="x-handlebars" id="feeds_tmpl">
<ul>
	{{#each entries}}
	<li>
		<img src="{{favico s_link}}" class="left r10p">
		<a href="{{s_link}}" target="_blank">{{cut s_feed_name 30}}</a>
	</li>
	{{/each}}
</ul>
</script>
