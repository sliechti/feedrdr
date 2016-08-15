
<script type="text/x-handlebars-template" id="left-menu-profiles-tmpl">
	{{log 'left menu profiles tmpl' profiles}}
	{{#each profiles}}
	<div>
		<a href="#" onclick="selectProfile({{l_profile_id}}, true); return false;">
			{{s_profile_name}}
		</a>
		<a class="pr20p right" href="${baseUrl}/pages/settings.jsp#/v/pro">
			<i class="fa fa-cog fade-color"></i>
		</a>
	</div>
	{{/each}}
</script>
