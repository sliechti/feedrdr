
<script type="text/x-handlebars-template" id="left-menu-profiles-tmpl">
	{{log 'left menu profiles tmpl' profiles}}
	{{#each profiles}}
	<div>
		<a href="#" onclick="selectProfile({{l_profile_id}}, true); return false;">
			{{s_profile_name}}
		</a>
	</div>
	{{/each}}
</script>
