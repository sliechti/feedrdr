<!--
how to add feeds step: show images of locations.
would be cool if /blog could be added as a news source.
 -->
<!DOCTYPE html>
<html>
<head>
<title>Welcome</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<script src="${baseUrl}/js/vendor/jquery.js"></script>
<script src="${baseUrl}/js/app/wizard.js"></script>
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
<style>
	.-hide {
		display: block;
	}
	.header {
		margin-bottom: 0px;
	}
</style>
</head>
<body class="margin0">
	<div class="header">
		<div class="table center w80p block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span>
				<img src="${baseUrl}/img/logo.svg" id="logo" class="vertical-middle" height="20" />
			</div>
		</div>
	</div>

	<div class="center w80p min250">
		<div class="max500">
		<div id="welcome">
			<h3>Welcome to feedrdr!</h3>
			<p>Let's get you started.</p>
			<ul class="left20">
				<li class="password-setup">Set-up a password</li>
			</ul>
		</div>

		<div class="password-setup" class="box w100p">
			<form class="w100p" id="password-form" method="post" onsubmit="return false;">
				<input class="block w100p bottom5'" type="password" name="pwd1" placeholder="password"> <input class="block w100p bottom5"
					type="password" name="pwd2" placeholder="confirm your password">
				<div class="none" id="password-msg"></div>
				<button id="btn-pwd-change" class="block w100p btn" onclick="changePassword(this, $('#password-form'))">Set-up password</button>
			</form>
		</div>

		<div class="thanks hide">
			<h3>You are all set</h3>
			<p>
				We really hope you enjoy feedrdr as much as we loved creating it.<br>
				We are adding something new every week so make sure you follow us in
				<a href="https://www.facebook.com/feedrdr">Facebook</a> and
				<a href="https://twitter.com/feedrdrco">Twitter</a>.
			</p>
			<p>
				<b>Adding content:</b><br>
				The fastest way to subscribe to new feeds are <a href="${baseUrl}/pages/collections.jsp">collections</a>
				and <a href="${baseUrl}/pages/import.jsp">importing your own OPML file</a>,
				you also can add the RSS or Atom feed with the URL too.
			</p>
			<p>Let us know what you like or dislike, or what you would like us to add. <p>
			<p>You can always contact me at steven@feedrdr.co, or feel free to
				<a href="mailto:steven@feedrdr.co?subject=just saying hi!&body=just signed up and only wanted to say hi! :)">
				send me an email if you just want to say hi :)</a>
			</p>
			<a href="${home}" onclick="fbq('track', 'CompleteRegistration');" class="block plain-btn w100p success">Start reading</a>
		</div>

		<div><p>&nbsp;</p></div>
		</div>
	</div>
<script>
	fbq('track', 'Lead');
</script>
</body>
</html>
