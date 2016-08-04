
<script type="x-handlebars" id="nav_profiles_tmpl">
    {{#each profiles}}
    <div class="text-center" style="background-color: \#{{s_color}}">
        <a href="#" onClick="selectProfile({{l_profile_id}}, true); return false;">
		{{s_profile_name}}
		</a>
    </div>
    {{/each}}
</script>
