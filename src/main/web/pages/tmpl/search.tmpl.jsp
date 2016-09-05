
<script id="search-sources-result" type="text/x-handlebars-template">
<div id="sources-result" class="">
	{{#each entries}}
	<div id="source-{{l_xml_id}}" class="row" >
		<div class="title">
			<a href="#/s/{{l_xml_id}}">{{s_title}}</a>
		</div>
		<div class="actions">
			<a href="{{s_link}}">
				<i class="fa fa-external-link" />
			</a>
			<a href="${baseUrl}/add?source={{l_xml_id}}">
				<i class="fa fa-plus-circle" />
			</a>
		</div>
	</div>
	{{/each}}
</div>
</script>
