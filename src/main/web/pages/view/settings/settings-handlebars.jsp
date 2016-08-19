<script id="tmpl-settings-profile-list" type="text/x-handlebars-template">
{{#each profiles}}
	{{>tmplprofile}}
{{/each}}
</script>

<script id="tmpl-settings-profile" type="text/x-handlebars-template">
	{{log this}}
	<div id="profile_{{l_profile_id}}" class="profile-row w100p row">
		<div class="w70p">
			<input  type="text" placeholder='profile' id="name_profile_{{l_profile_id}}" value="{{s_profile_name}}">
		</div>
		<div class="settings row">
			<div>
				<input placeholder='color' size="4" type="text" data-color="{{s_color}}"
					id="picker_profile_{{l_profile_id}}" value="{{s_color}}">
			</div>
			<div class="actions">
				<a href="#" onclick="deleteProfile({{l_profile_id}}); return false;">
					<i class="right fa fa-remove"></i>
				</a>
			</div>
		</div>
    </div>
</script>
