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
<script src="${baseUrl}/js/jquery.js"></script>
<script src="${baseUrl}/js/wizard.js"></script>
<style>
	.-hide {
		display: block;
	}
</style>
</head>
<body class="margin0">
	<div class="header">
		<div class="table center w80p block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span>
				<img src="${baseUrl}/img/logo.svg" id="logo" class="vertical-middle" height="30" />
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
				<li class="content-info">How to add content</li>
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

		<div class="content-setup hide">
			<h3>How to subscribe to news feeds</h3>
			<ul>
				<li>You can add feeds with the URL</li>
				<li>You can import your OPML files</li>
				<li>You can add curated collections</li>
			</ul>
			<p>
				<button class="w100p block btn" onclick="goNext(); return false;">Got it</button>
			</p>
		</div>

		<div class="thanks hide">
			<h3>You are all set</h3>
			<p>
				We really hope you enjoy feedrdr as much as we loved creating it.
				We are adding something new every week so make sure you follow us in
				<a href="https://www.facebook.com/feedrdr">Facebook</a> and
				<a href="https://twitter.com/feedrdrco">Twitter</a>
			</p>
			<p>Let us know what you like or dislike, or what you would like us to add. <p>
			<p>You can always contact me at <a href="mailto:steven@feedrdr.co?subject=just saying hi!&body=just signed up and only wanted to say hi! :)">Steven Liechti</a></p>
			<a href="${home}" class="block plain-btn w100p success">Start reading</a>
		</div>

		<div><p>&nbsp;</p></div>
		</div>
	</div>
</body>
</html>
