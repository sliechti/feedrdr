<%@page import="feedreader.utils.Validate"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.log.Logger"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.utils.TextUtils"%>
<%@page import="java.io.InputStream"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.config.FeedAppConfig"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= FeedAppConfig.APP_NAME %></title>
        
<link href="<%= PageUtils.getPath("/css/bootstrap.min.css") %>" rel="stylesheet" type="text/css"/>

<body>
    
<div style="font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; font-size: 14px; width: 400px; margin: auto">
    
<%
    String msg = "Unknown code.";
    int redirect = 5;
    
    String code = Parameter.asString(request, "code", "");
    
    if (!code.isEmpty()) 
    {
        UserData data = UsersTable.getFromForgotCode(code);

        if (data.getUserId() != 0) 
        {
            String pwd_1 = Parameter.asString(request, "pwd_1", "");
            String pwd_2 = Parameter.asString(request, "pwd_2", "");
            
            if (!pwd_1.isEmpty() && pwd_1.equals(pwd_2)) 
            {
                if (Validate.isValidPassword(pwd_1))
                {
                    int r = UsersTable.setNewPassword(data, pwd_1);
                    if (r >= 0)
                    {
                        msg = "<h4>Password changed.</h4>";
                        redirect = 7;
                    }
                    else
                    {
                        msg = "The password couldn't be changed. We will have a look and fix this issue as soon "
                                + "as possible. Please try again later.";
                        redirect = 7;                    
                    }
                }
                else
                {
                    msg = "The password you chose is invalid: " + Validate.getPasswordRules() + "<br>" +
                            "Please <a href='' onClick='location.history(-1);return false'>go back</a>" + 
                            " and try again.";
                    redirect = 0;
                }
            }
            else if (!pwd_1.isEmpty())
            {
                msg = "Passwords don't match. Please <a href='' onClick='location.history(-1);return false'>go back</a>"
                        + " and try again.";
                redirect = 0;            
            }
            else
            {
                msg = "";
                redirect = 0;
%>
            
            <form method="POST" action="">
                <input type="hidden" name="code" value="<%= code %>">
                <label for="pwd_1">New password
                    <input class="form-control" type="password" name="pwd_1">
                <label for="pwd_2">Password confirmation
                <input class="form-control" type="password" name="pwd_2">
                <input type="submit" class="btn btn-primary btn-block" name="submit" value="Change password">
            </form>

<%
            }
        }
    } 
%>

<%= msg %>
<br>
<br>

<% if (redirect > 0) { %>
You will be redirected to the <a href="<%= FeedAppConfig.BASE_APP_URL %>/">login page</a> in <label id='seconds'>7</label> seconds.

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
<% } %>
</div>

</body>
</html>
