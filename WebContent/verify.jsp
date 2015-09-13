<%@page import="feedreader.log.Logger"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.utils.TextUtils"%>
<%@page import="java.io.InputStream"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.config.FeedAppConfig"%>

<%
    String msg = "Unknown code.";
    int redirect = 5;
    
    String code = Parameter.asString(request, "code", "");
    if (!code.isEmpty()) 
    {
        UserData data = UsersTable.getFromRegCode(code);
        if (data.getUserId() != 0) 
        {
            UsersTable.verify(data);
            
            InputStream is = getServletContext().getResourceAsStream("/WEB-INF/tmpl/accountverified.tmpl");
            StringBuilder sb = TextUtils.toStringBuilder(is, new StringBuilder(), true);
            msg = sb.toString();
            msg = msg.replace("{NAME}", data.getScreenName());
            redirect = 7;
        }
    }
    
%>
<div style="font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; font-size: 14px; width: 400px; margin: auto">
    <%= msg %>
<br>
<br>
You will be redirected to the <a href="<%= FeedAppConfig.BASE_APP_URL %>/">login page</a> in <label id='seconds'>7</label> seconds.
</div>

<script type="text/javascript">
    var seconds = <%= redirect %>;
    function redirect()
    {    
        seconds--;
        document.getElementById('seconds').innerHTML = seconds;
        if (seconds <= 0) {
            location.assign('<%= FeedAppConfig.BASE_APP_URL  %>/')
        }
        window.setTimeout(redirect, 1000);
    };
    window.setTimeout(redirect, 1000);
    document.getElementById('seconds').innerHTML = <%= redirect %>;
</script>
