<%@page import="feedreader.config.FeedAppConfig"%>

<%! static String baseUrl = FeedAppConfig.BASE_APP_URL; %>

<script id="all_subscriptions_tmpl" type="text/x-handlebars-template">
    <ul class="sub_list">
{{#each subscriptions}}
        <li><a class="all_subs" href="#/view/{{l_subs_id}}">{{s_subs_name}}</a></li>
{{/each}}
    </ul>
</script>

<script id="subscription_details_tmpl" type="text/x-handlebars-template">
<h3 style="margin-top: -40px;">{{s_subs_name}}<br><a class="form-title" href="<%= baseUrl %>/pages/reader.jsp#/s/{{l_xml_id}}">view source</a></h3>
    
<h4>Subscription status:</h4>
<pre>
Feed's URL: <a href="{{s_xml_url}}" target="_new">{{s_xml_url}}</a>
Last checked at: {{formatUnixTs t_checked_at}}
Error count: {{h_error_count}}
Error code : {{h_error_code}}
Last error : {{s_last_error}}
Total entries found : <b>{{i_total_entries}}</b>
Gave up on source? : <b>{{b_gaveup}}</b>
</pre>

<form id="subscriptionForm">
    <div style="margin-bottom: 20px;">
        <label for="name">Subscription's name</label>
        <input class="form-control" type="text" name="name" value="{{s_subs_name}}">
    </div>
    <div>
        <input class="btn btn-default" type="submit" value="Save changes">
        <button class="btn btn-danger right" onClick="removeSubscription({{l_subs_id}});">Unsubscribe</button>
    </div>
</form>
<hr>
</script>

<script id="profile_stream_groups_tmpl" type="text/x-handlebars-template">
    Used in stream group <b>{{name}}</b>&nbsp;
            <a href="" onClick="removeFromStreamGroup({{subid}}, {{streamid}});return false;">remove</a><br>
            used in profiles: <br>
    {{#each profiles}}
            {{s_profile_name}}&nbsp;
    {{/each}}
    <br>
<hr>
</script>