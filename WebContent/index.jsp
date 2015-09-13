<%@page import="feedreader.config.OAuthConfig"%>
<%@page import="feedreader.utils.SimpleEncryption"%>
<%@page import="feedreader.store.DBFields"%>
<%@page import="feedreader.utils.Validate"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.security.UserSession"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Parameter"%>

<%
	String info = "";
	String err = "";

	if (request.getMethod() == "POST") {
		if (!Parameter.asString(request, "login", "").isEmpty()) {
			int r = UserSession.authenticate(request, response);
			switch (r) {
				case 1 :
					PageUtils.gotoHome(response);
					return;

				case 0 :
					err = "Couldn't initialize the user session. If you are still experiencing this issue "
							+ "after a couple of retries, please let us know.";
					break;

				case -1 :
					err = "Wrong username/email or password combination.";
					break;
			}
		}

		if (Parameter.asString(request, "signup", "").equalsIgnoreCase(
				"signup")) {
			UserSession.createNew(response, request);
			return;
		};

		if (!Parameter.asString(request, "recover_email", "").isEmpty()) {
			String recoverEmail = Parameter.asString(request,
					"recover_email", "");
			if (Validate.isValidEmailAddress(recoverEmail)) {
				UserData data = UsersTable.get(Parameter.asString(
						request, "recover_email", ""));
				if (data.getUserId() > 0) {
					byte[] code = SimpleEncryption.encrypt(
							FeedAppConfig.ENC_KEY_REOVER_EMAIL_CODE,
							true, data.getEmail());
					int r = UsersTable.setForgotPassword(
							data.getUserId(), new String(code));
					if (r >= 0) {
						info = "An email will be sent to "
								+ Parameter.asString(request,
										"recover_email", "user@domain")
								+ ", please contact us if you don't receive it in the next couple of minutes. Thank you.";
					} else {
						err = "There was a problem. We were informed and will have a look as soon as possible.";
					}
				};
			} else {
				err = "Invalid email. ";
			}
		}
	}

	long userId = UserSession.getUserId(request);
	if (userId > 0) {
		PageUtils.gotoHome(response);
		return;
	}

	String minjs = (Environment.isProd() ? ".min.js" : ".js");
	String mincss = (Environment.isProd() ? ".min.css" : ".css");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><%=FeedAppConfig.APP_NAME%></title>
<link href="<%=PageUtils.getPath("/css/bootstrap" + mincss)%>"
	rel="stylesheet" type="text/css" />
<link href="<%=PageUtils.getPath("/css/default" + mincss)%>"
	rel="stylesheet" type="text/css" />
<%
	if (Environment.isProd()) {
%>
<link href="<%=PageUtils.getPath("/css/auth-buttons-prod" + mincss)%>"
	rel="stylesheet" type="text/css" />
<%
	} else {
%>
<link href="<%=PageUtils.getPath("/css/auth-buttons-dev" + mincss)%>"
	rel="stylesheet" type="text/css" />
<%
	}
%>
<link href="<%=PageUtils.getPath("/css/auth-buttons" + mincss)%>"
	rel="stylesheet" type="text/css" />
<script src="<%=PageUtils.getPath("/js/jquery" + minjs)%>"
	type="text/javascript" /></script>
<script src="<%=PageUtils.getPath("/js/bootstrap" + minjs)%>"
	type="text/javascript"></script>
<script src="<%=PageUtils.getPath("/js/hello" + minjs)%>"
	type="text/javascript"></script>
<script src="<%=PageUtils.getPath("/js/hello.init" + minjs)%>"
	type="text/javascript"></script>
</head>

<body>
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.3&appId=1587611758138796";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
	<div class="login">
		<%
			if (!err.isEmpty()) {
		%>
		<div id="error" class="alert alert-danger alert-dismissible">
			<button type="button" class="close" data-dismiss="alert">
				<span aria-hidden="true"></span><span class="sr-only">Close</span>
			</button>
			<p id="error_text"><%=err%></p>
		</div>
		<%
			}
		%>
		<%
			if (!info.isEmpty()) {
		%>
		<div id="info" class="alert alert-info alert-dismissible">
			<button type="button" class="close" data-dismiss="alert">
				<span aria-hidden="true"></span><span class="sr-only">Close</span>
			</button>
			<p id="info_text"><%=info%></p>
		</div>
		<%
			}
		%>
		<a href="https://github.com/sliechti/feedrdr">
			<img style="position: absolute; top: 0; right: 0; border: 0;" src="https://camo.githubusercontent.com/a6677b08c955af8400f44c6298f40e7d19cc5b2d/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f677261795f3664366436642e706e67" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_right_gray_6d6d6d.png">
		</a>
		
		<div><center><p>A feed reader to keep track of all the content you care about.
		<a href="http://rdr.uservoice.com/">user feedback.</a></p></center><br><br></div>

		
		<div id="signup" class="noshow">
			<form id="signup_form" action="" role="form" method="POST">
				<input type="text" placeholder="Pick a username"
					class="form-control" name="<%=Constants.INPUT_SCREEN_NAME%>"
					required="true"> <input type="text"
					placeholder="Your email" class="form-control"
					name="<%=Constants.INPUT_EMAIL_NAME%>" required="true"> <input
					type="password" placeholder="Create a password"
					class="bottom10 form-control" required="true"
					name="<%=Constants.INPUT_PWD_NAME%>"> <input type="submit"
					class="btn btn-primary btn-block" name="signup" value="Signup"
					id="btn_signup">
			</form>
		</div>
		<div id="login">
			<form id="login_form" action="" role="form" method="POST">
				<input type="text" placeholder="Username or email"
					class="form-control" name="<%=Constants.INPUT_EMAIL_NAME%>"
					required="true" id="txt_username"> <input type="password"
					placeholder="password" class="bottom10 form-control"
					required="true" name="<%=Constants.INPUT_PWD_NAME%>"
					id="txt_password"> <input type="submit"
					class="btn btn-primary btn-block" name="login" value="login"
					id="btn_login">
				<div class="small text-right">
					<input type="checkbox" name="<%=Constants.INPUT_REMEMBER_ME%>">&nbsp;<span>Keep
						me logged in (I trust this computer)</span>
				</div>
			</form>
			<div class="small text-right">
				<a href='' onclick="$('#forgot').toggle();return false;">Forgot
					your password?</a>
			</div>
			<div id='forgot' style="margin-top: 10px" class="noshow">
				<form method="POST" action="">
					<input type="text" name="recover_email" class="form-control"
						placeholder="Your email" required="true" st> <input
						type="submit" class="btn btn-info btn-block" name="recover"
						value="Recover password">
				</form>
			</div>
		</div>
		<div id="signup_txt" class="text-center">
			<br> <a class="text-center block"
				onclick="$('#login').hide();$('#signup').show();$(this).parent().hide();$('#signin_txt').show();return false;"
				href="">Sign up in less than a minute</a><br>
		</div>
		<div id="signin_txt" class="noshow text-center">
			<br> <a class="text-center block"
				onclick="$('#login').show();$('#signup').hide();$(this).parent().hide();$('#signup_txt').show();return false;"
				href="">Sign in with your account</a><br>
		</div>
	</div>

	<div class="login text-center">

		<p class="lead">Or</p>
		<a class="btn-auth btn-facebook" href="#"
			onclick="hello('facebook').login({force: true, scope: 'public_profile,email'});return false;">
			Sign in with <b>Facebook</b>
		</a><br> <br> <a class="btn-auth btn-google" href="#"
			onclick="hello('google').login({force : true, scope: 'profile email'});return false;">Sign
			in with <b>Google+</b>
		</a><br> <br> <a class="btn-auth btn-windows" href="#"
			onclick="hello('windows', {scope: 'wl.emails,wl.basic'}).login();return false;">
			Sign in with <b>Windows</b>
		</a><br> <br>
		<%
			/*
			       <div id="show-more-options">
			           <a href="" onclick="$('#more-options').show();$('#show-more-options').hide();return false;">More options</a>
			       </div>
			       <div id="more-options" style="display: none">
			           <a class="btn-auth btn-twitter" href="#" onclick="alert('not implemented, yet')">
			               Sign in with <b>Twitter</b>
			           </a><br><br>

			           <a class="btn-auth btn-github" href="#" onclick="alert('not implemented, yet')">
			               Sign in with <b>GitHub</b>
			           </a><br><br>

			           <a class="btn-auth btn-yahoo" href="#" onclick="alert('not implemented, yet')">
			               Sign in with <b>Yahoo!</b>
			           </a><br><br>

			           <a class="btn-auth" href="#" onclick="alert('not implemented, yet')">
			               Sign in with <b>Linkedin</b></a>
			           <br><br>

			           <a class="btn-auth btn-openid" href="#" onclick="alert('not implemented, yet')">
			               Sign in with <b>Openid</b>
			           </a>
			           <br><br>
			           <a href="" onclick="$('#more-options').hide();$('#show-more-options').show();return false;">Show less options</a>
			       </div>
			 */
		%>
		
		<p><div class="fb-like" data-href="https://www.facebook.com/pages/feedrdrco/378471115664584" 
		data-layout="button" data-action="like" data-show-faces="true" data-share="true"></div>
		<br><br>
		<a href="https://twitter.com/feedrdrco" class="twitter-follow-button" data-show-count="false" data-show-screen-name="false">Follow @feedrdrco</a>
		<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
		</p>
	</div>
	
	<script type="text/javascript">
        initHello('<%=OAuthConfig.getFbKey()%>', '<%=OAuthConfig.GOOGLE_KEY%>', '<%=OAuthConfig.LIVE_KEY%>');
		setHelloCallbacks(function(data) {
			console.log("on session ");
			console.log(data);
		}, function(data) {
	<%if (Environment.isProd()) {%>
		var ext = '';
	<%} else {%>
		var ext = '&debug=true';
	<%}%>
		location.href = 'login/oauth.jsp?network=' + data.network
					+ "&token=" + data.authResponse.access_token + ext;
		}, function(data) {
		});
	</script>
</body>
</html>