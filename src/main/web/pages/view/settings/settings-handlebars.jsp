<script id="tmpl-settings-profile-list" type="text/x-handlebars-template">
{{#each profiles}}
	{{>tmplprofile}}
{{/each}}
</script>

<script id="tmpl-settings-profile" type="text/x-handlebars-template">
	<div id="profile_{{l_profile_id}}" class="profile-row w100p row">
		<div class="w70p">
			<input  type="text" id="name_profile_{{l_profile_id}}" value="{{s_profile_name}}">
		</div>
		<div class="settings row">
			<div>
				<input size="4" type="text" data-color="{{s_color}}"
					id="cpp-{{l_profile_id}}">
			</div>
			<div class="actions">
				<i class="right fa fa-remove" onclick="deleteProfile({{l_profile_id}})"></i>
			</div>
		</div>
    </div>
</script>
