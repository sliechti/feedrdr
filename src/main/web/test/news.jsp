<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
//    int feedId = 319;
    long id = Parameter.asLong(request, "streamId", 0);
    
//    Database.execute("update feedreader.userfeedsubscriptions set s_time_filter = '' where l_subs_id = 1591");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="../js/jquery.js" type="text/javascript"></script>
        <script src="../js/jquery.visible.js" type="text/javascript"></script>
        <script src="../js/handlebars.js" type="text/javascript"></script>
        <script src="../js/global.js" type="text/javascript"></script>
        <script src="news.js" type="text/javascript"></script>
        <link rel="stylesheet" href="../css/bootstrap.css" type="text/css">
        <link rel="stylesheet" href="../css/default.css" type="text/css">
        <title>News PoC</title>
        <style>
            .read {
                background-color: #AFE8FF;
            }
        </style>
    </head>
    <body>
        <h1>News Stream for ID <%= id %>, unread <label id="unread">unknown</label></h1>        
        <div class="container">
            <div class="row">
                <div class="row" id="stream_entries"></div>
                <div class="row" id="stream_entries_footer">
                    <a id="more" href="" class="noshow" onclick="more(); return false;">more</a><br><br>
                    <a id="mark_all_read" class="noshow" href="" onclick="markAllRead(); return false;">mark all read</a>
                </div>
            </div>
        </div>
    </body>
    
    <script id="news_stream_tmpl" type="text/x-handlebars-tempalte">
        {{#each entries}}
            <p class="news" id="{{l_entry_id}}" data-p="{{position}}" data-pub_date={{t_pub_date}}>
            <b>{{l_xml_id}} {{l_entry_id}} {{s_title}}</b><br>
            {{s_link}}<br>
            {{t_pub_date}}<br>
            {{formatUnixTs t_pub_date}}<br>
            <a href="" onclick="markRead({{l_entry_id}}); return false;">mark read</a>
            </p>
        {{/each}}
    </script>
    <script type="text/javascript">
        $(document).ready(function()
        {
            console.log('loaded.');
            initNews();
            loadStreamEntries(<%= id %>, 0);
        })
    </script>
</html>
