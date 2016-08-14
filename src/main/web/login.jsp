<!DOCTYPE html>
<html language="${language}">
<head>
<title>Sign in</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<script src="${baseUrl}/js/vendor/jquery.min.js" type="text/javascript" /></script>
<link rel="stylesheet" href="${baseUrl}/css/login.css" />
</head>
<body>
	<div id="login">
		<div class="login-header">
			<a href="${baseUrlLink}">
				<img src="img/app-logo.svg" height="100"/>
			</a>
			<p>Sign in</p>
		</div>
		<div class="login-content">
			${error}
			<form method="post" action="">
				<input type="hidden" name="login" value="1" />
				<input type="text" class="w100p" placeholder="Email" name="email">
				<div class="right">
					<a href="password_reset" tabindex="10">Forgot password?</a>
				</div>
				<input type="password" class="w100p" placeholder="Password" name="pwd">
				<div class="right fade font-small">
					<label>
						<input type="checkbox" name="remember_me" value="1">Remember me
					</label>
				</div>
				<button class="w100p">Sign in</button>
			</form>
		</div>
		<div class="footer">
			New to feedrdr.co?<br> <a href="${baseUrlLink}">Create an account</a>
		</div>
	</div>
	<script>
		$(document).ready(function() {
			$("form input:visible").first().focus()
		})
	</script>
</body>
</html>
