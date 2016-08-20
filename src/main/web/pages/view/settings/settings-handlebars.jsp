<script id="tmpl-settings-profile-list" type="text/x-handlebars-template">
{{#each profiles}}
	{{>tmplprofile}}
{{/each}}
</script>

<script id="tmpl-settings-profile" type="text/x-handlebars-template">
	<div id="profile_{{l_profile_id}}" class="profile-row w100p row">
		<div class="name">
			<input onkeydown="spActivateBurron('#btn-up-{{l_profile_id}}')"
				type="text" placeholder='Profile name' id="name_profile_{{l_profile_id}}"
				value="{{s_profile_name}}">
		</div>
		<div class="settings row">
			<div>
				<input placeholder='Color' size="4" type="text" data-color="{{s_color}}"
					id="picker_profile_{{l_profile_id}}" value="{{s_color}}">
			</div>
			<div class="actions">
				<input type="button" id="btn-up-{{l_profile_id}}" value="Update"
					onclick="saveProfile({{l_profile_id}}, this)" disabled="disabled">
				<input type="button" value="Delete" class="danger"
					onclick="deleteProfile({{l_profile_id}}); return false;">
			</div>
		</div>
    </div>
</script>
