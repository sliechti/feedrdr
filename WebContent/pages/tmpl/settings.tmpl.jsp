
<script id="all_profiles_settings_tmpl" type="text/x-handlebars-template">
{{#each profiles}}
    <div id="profile_{{l_profile_id}}" class="row paddingall10">
        <div class="col-lg-6">
            <input class="form-control" type="text" id="name_profile_{{l_profile_id}}" value="{{s_profile_name}}">
        </div>
        <div class="col-lg-2 text-right">
            <input size="6" class="form-control-static" type="text" data-color="{{s_color}}" id="picker_profile_{{l_profile_id}}">
        </div>
        <div class="col-lg-4 text-right">
            <button class="btn btn-primary" onClick="saveProfile({{l_profile_id}})">save</button>
            <button class="btn btn-danger" onClick="deleteProfile({{l_profile_id}})">delete</button>
        </div>
    </div>
{{/each}}
</script>
