
<script type="x-handlebars" id="nav_profiles_tmpl">
    {{#each profiles}}
    <div class="text-center">
		<div class="left w5px" style="background-color: \#{{s_color}}">
			&nbsp;
		</div >
        <a href="#" onClick="selectProfile({{l_profile_id}}, true); return false;">
		{{s_profile_name}}
		</a>
		<div class="right w5px" style="background-color: \#{{s_color}}">
			&nbsp;
		</div >
    </div>
	<div class="profile-line" style="background-color: \#{{s_color}}"></div>
    {{/each}}
</script>
