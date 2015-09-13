
<script type="x-handlebars" id="nav_profiles_tmpl">
    {{#each profiles}}
    <li><div class="navbar-div">
        <span style="float: left; background-color: \#{{s_color}}">&nbsp;</span>
        <a href="#" onClick="selectProfile({{l_profile_id}}, true); return false;">{{s_profile_name}}</a>
    </div></li>
    {{/each}}
    <li>
        <div class="navbar-div block">
            <a href="" onclick="showCreateNewProfile(); return false;">New <span class="glyphicon glyphicon-plus text-right"></span></a>
        </div>
    </li>    
</script>
