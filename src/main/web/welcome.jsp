<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>feedrdr.co - open source rss news reader</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<meta name="google-site-verification" content="w2qoFGms5i-1odj9Jj2G3CTCtMokK79Oi93mwglzNkk" />
<meta name="msvalidate.01" content="13BFF01091BF323BC721335417DC72C4" />
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<script src="${baseUrl}/js/jquery${minifiedStr}.js" type="text/javascript" /></script>
<script src="${baseUrl}/js/hello${minifiedStr}.js" type="text/javascript"></script>
<script src="${baseUrl}/js/hello.init${minifiedStr}.js" type="text/javascript"></script>
</head>
<body>
	<div id="fb-root"></div>
	<script>
		(function(d, s, id) {
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id))
				return;
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.3&appId=1587611758138796";
			fjs.parentNode.insertBefore(js, fjs);
		}(document, 'script', 'facebook-jssdk'));
	</script>

	<div class="body">
		<div class="header">
			<div class="center w80p block">
				<div class="left logo">&nbsp;</div>
				<div class="right header-btn">
					<a href="${baseUrl}/login" onclick="$('#login').show()">Sign in</a>
				</div>
			</div>
		</div>
		<div class="content">
			<div id="center-piece" class="table center w80p">
				<div class="cell w50p">
					<p class="intro">
						An <a href="http://github.com/sliechti/feedrdr">Open Source</a> RSS news reader to help you keep track of all the content you care
						about, getting better every week and made with &#9825; in NYC.
					</p>
				</div>
				<div class="cell text-center w50p vertical-middle">
					<form id="start-reading">
						<input type="text" class="block w100p" placeholder="your@email">
						<button class="block w100p btn">start reading</button>
					</form>
					<p class="lead">Or</p>
					<p class="lead">
						<a class="btn-auth btn-facebook" href="#" onclick="hello('facebook').login({force: true, scope: 'public_profile,email'});return false;">
						<i class="fa fa-facebook r20p" aria-hidden="true"> </i></a>
						<a class="btn-auth btn-google" href="#" onclick="hello('google').login({force : true, scope: 'profile email'});return false;">
						<i class="fa fa-google r20p" aria-hidden="true"> </i></a>
						<a class="btn-auth btn-windows" href="#" onclick="hello('windows', {scope: 'wl.emails,wl.basic'}).login();return false;">
						<i class="fa fa-windows r20p" aria-hidden="true"> </i></a>
					</p>

				</div>
			</div>
			<div id="signup" class="hide">
				<form id="signup_form" action="" role="form" method="POST">
					<input type="text" id="pick" placeholder="Pick a username" class="form-control" name="display_name" required="true"> <input
						type="text" placeholder="Your email" class="form-control" name="email" required="true"> <input type="password"
						placeholder="Create a password" class="bottom10 form-control" required="true" name="pwd"> <input type="submit"
						class="btn btn-primary btn-block" name="signup" value="Signup" id="btn_signup">
				</form>
			</div>
		</div>

		<div class="footer">
			<ul>
				<li><a href="#">About</a></li>
				<li><a href="https://twitter.com/feedrdrco">Twitter</a></li>
				<li><a href="https://www.facebook.com/feedrdr">Facebook</a></li>
			</ul>
		</div>
	</div>
</body>
</html>

<script>
	!function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/
				.test(d.location) ? 'http' : 'https';
		if (!d.getElementById(id)) {
			js = d.createElement(s);
			js.id = id;
			js.src = p + '://platform.twitter.com/widgets.js';
			fjs.parentNode.insertBefore(js, fjs);
		}
	}(document, 'script', 'twitter-wjs');
</script>

<script type="text/javascript">
	initHello('${oauthFacebooKey}', '${oauthGoogleKey}', '${oauthWindowsKey}');
	setHelloCallbacks(function(data) {
		console.log("on session ");
		console.log(data);
	}, function(data) {
		var ext = '${debugOauth}';
		location.href = 'login/oauth.jsp?network=' + data.network + "&token="
				+ data.authResponse.access_token + ext;
	}, function(data) {
	});
</script>

<script type="text/javascript">
	var _paq = _paq || [];
	_paq.push([ 'trackPageView' ]);
	_paq.push([ 'enableLinkTracking' ]);
	(function() {
		var u = "//feedrdr.piwikpro.com/";
		_paq.push([ 'setTrackerUrl', u + 'piwik.php' ]);
		_paq.push([ 'setSiteId', 1 ]);
		var d = document, g = d.createElement('script'), s = d
				.getElementsByTagName('script')[0];
		g.type = 'text/javascript';
		g.async = true;
		g.defer = true;
		g.src = u + 'piwik.js';
		s.parentNode.insertBefore(g, s);
	})();
</script>
<noscript>
	<p>
		<img src="//feedrdr.piwikpro.com/piwik.php?idsite=1" style="border: 0;" alt="" />
	</p>
</noscript>
