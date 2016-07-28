<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>feedrdr.co - An open source RSS news reader</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<meta name="google-site-verification" content="w2qoFGms5i-1odj9Jj2G3CTCtMokK79Oi93mwglzNkk" />
<meta name="msvalidate.01" content="13BFF01091BF323BC721335417DC72C4" />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-title" content="feedrdr.co">
<meta name="application-name" content="feedrdr.co">
<link rel="apple-touch-startup-image" href="startup.png">
<link rel="apple-touch-icon" href="img/app-196x196.png"/>
<link rel="apple-touch-icon-precomposed" sizes="128x128" href="img/app-128x128.png">
<link rel="shortcut icon" sizes="196x196" href="img/app-196x196.png">
<link rel="shortcut icon" sizes="128x128" href="img/app-128x128.png">
<link rel="shortcut icon" href="${baseUrl}/favicon.ico" type="image/x-icon">
<link rel="icon" href="${baseUrl}/favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<script src="${baseUrl}/js/vendor/jquery.min.js" type="text/javascript" /></script>
<script src="${baseUrl}/js/vendor/hello.min.js" type="text/javascript"></script>
<script src="${baseUrl}/js/vendor/hello.init.min.js" type="text/javascript"></script>
<c:if test="${not isLocal}">
<!-- Facebook Pixel Code -->
<script>
!function(f,b,e,v,n,t,s){if(f.fbq)return;n=f.fbq=function(){n.callMethod?
n.callMethod.apply(n,arguments):n.queue.push(arguments)};if(!f._fbq)f._fbq=n;
n.push=n;n.loaded=!0;n.version='2.0';n.queue=[];t=b.createElement(e);t.async=!0;
t.src=v;s=b.getElementsByTagName(e)[0];s.parentNode.insertBefore(t,s)}(window,
document,'script','https://connect.facebook.net/en_US/fbevents.js');

fbq('init', '504829576373713');
fbq('track', "PageView");</script>
<noscript><img height="1" width="1" style="display:none"
src="https://www.facebook.com/tr?id=504829576373713&ev=PageView&noscript=1"
/></noscript>
<!-- End Facebook Pixel Code -->
</c:if>
</head>
<body class="margin0">
	<div class="header">
		<div class="table center w80p block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span>
				<img src="img/logo.svg" id="logo" class="vertical-middle" height="30" />
			</div>
			<div onclick="location.href='login'" class="pointer cell vertical-middle right header-btn">
				<a href="login">Sign in</a>
			</div>
		</div>
	</div>
	<div class="content">
		<div id="center-piece">
			<div class="cell w50p">
				<p class="intro">
					An Open Source RSS news reader to help you manage and keep track of all the content
					you care about, getting better every week and made with &#9825; in NYC.
				</p>
			</div>
			<div class="cell text-center w50p vertical-middle bottom40">
				<c:if test="${not empty invalidEmail}">
					<div class="warning">
					The email '${email}' appears to be invalid, please try another one.
					</div>
				</c:if>
				<c:if test="${not empty emailKnown}">
					<div class="warning">
					The email is already registered, please <a href="login">sign in</a>
					or <a href="password_reset">reset you password</a>
					</div>
				</c:if>
				<form id="start-reading" method="post" action="signup">
					<input type="text" class="block w100p" id="email" name="email" placeholder="your@email">
					<button class="block w100p btn">start reading</button>
				</form>
				<p class="lead">Or</p>
				<p class="lead">
					<a class="btn-auth btn-facebook" href="#" onclick="hello('facebook').login({force: true, scope: 'public_profile,email'});return false;">
					<i class="fa fa-facebook r20p" aria-hidden="true"></i></a>
					<a class="btn-auth btn-google" href="#" onclick="hello('google').login({force : true, scope: 'profile email'});return false;">
					<i class="fa fa-google r20p" aria-hidden="true"></i></a>
					<a class="btn-auth btn-windows" href="#" onclick="hello('windows', {scope: 'wl.emails,wl.basic'}).login();return false;">
					<i class="fa fa-windows r20p" aria-hidden="true"></i></a>
				</p>
			</div>
		</div>
	</div>

	<div class="footer">
		<ul>
			<li><a href="http://github.com/sliechti/feedrdr">GitHub</a></li>
			<li><a href="https://twitter.com/feedrdrco">Twitter</a></li>
			<li><a href="https://www.facebook.com/feedrdr">Facebook</a></li>
		</ul>
	</div>

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

</body>


</html>

<script>
	$(document).ready(function() {
		$('#email').focus();
		fbq('track', 'ViewContent');
	});
</script>
<script>
	!function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
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
		var ext = '${oauthDebug}';
		location.href = 'login/oauth.jsp?network=' + data.network + "&token=" + data.authResponse.access_token + ext;
	}, function(data) {
	});
</script>

<c:if test="${not isLocal}">
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
</c:if>

