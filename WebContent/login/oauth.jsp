<%@page import="feedreader.oauth.FacebookOAuthProvider"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.security.UserSession"%>
<%@page import="java.security.AuthProvider"%>
<%@page import="feedreader.oauth.OAuthUser"%>
<%@page import="feedreader.oauth.WindowsLiveOAuthProvider"%>
<%@page import="feedreader.oauth.GoogleOAuthProvider"%>
<%@page import="feedreader.oauth.BaseOauthProvider"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.security.Parameter"%>

<%
    String network = Parameter.asString(request, "network", "");
    String token = Parameter.asString(request, "token", "").replaceAll(" ", "+");
    boolean debug = Parameter.asBoolean(request, "debug", false);
%>
<% if (debug) {%>
<script src="<%= PageUtils.getPath("/js/hello.min.js")%>" type="text/javascript"></script>
<script type="text/javascript">
    <% if (Environment.isProd()) { %>
    var fbAppId = '1587611758138796';
    <% } else { %>
    var fbAppId = '1587614741471831';
    <% }%>

    console.log(fbAppId);
    hello.init({google: '36068092155-cmv7gujodqru441o5kkorujs01pd7de6.apps.googleusercontent.com',
        facebook: '1587614741471831',
        windows: '0000000044135DC2'});

    hello.on('auth', function (data)
    {
        console.log(data);
    });

    hello.on('auth.logout', function (data)
    {
        location.href = '<%= FeedAppConfig.BASE_APP_URL%>';
    })
</script>
<% } %>
<%
    BaseOauthProvider oauthProvider;

    if (network.equals("facebook")) {
        oauthProvider = new FacebookOAuthProvider();
    } else if (network.equals("google")) {
        oauthProvider = new GoogleOAuthProvider();
    } else if (network.equals("windows")) {
        oauthProvider = new WindowsLiveOAuthProvider();
    } else {
        out.write("unknown network");
        return;
    }

    oauthProvider.setToken(token);

    if (!oauthProvider.authenticate()) {
        out.write("couldn't authenticate account <br>");
        out.write(oauthProvider.getUrl());
    }

    OAuthUser user = oauthProvider.getOauthUser();

    if (debug) {
        out.write(user.toString() + "<br>");
        out.write("<button onclick=\"hello.logout('" + network + "');\">logout</button><br>");
        out.write("<a href=\"" + request.getRequestURI() + "?network=" + network + "&token=" + token + "\">reload without debug</a>");
        return;
    }

    if (user.getEmail().isEmpty()) {
        out.write("empty email address. ");
        return;
    }

    // TODO: Validate email address. 
    UserData data = UsersTable.get(user.getEmail());
    if (data.getUserId() != 0) {
        UsersTable.saveToken(data.getUserId(), oauthProvider.getAuthType(), token);
    } else {
        UserData usrData = UsersTable.createNewUser(user.getEmail(), oauthProvider.getAuthType(), "",
                user.getLocale(), user.getName());
        if (usrData.getUserId() <= 0) {
            out.write("Couldn't create user. <br>");
            return;
        }
        data = usrData;
    }

    if (!UserSession.initUserSession(request, data)) {
        out.append("error initializing session. <br>");
    } else {
        PageUtils.gotoHome(response);
        return;
    }

%>