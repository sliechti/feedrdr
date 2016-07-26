<!DOCTYPE html>
<html>
<head>
<title>Sign in</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<script src="${baseUrl}/js/jquery${minifiedStr}.js" type="text/javascript" /></script>
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<link rel="stylesheet" href="${baseUrl}/css/login.css" />
</head>
<body>
	<div class="login">
		<div class="login-header">
			<img src="img/app-logo.svg" height="100"/>
			<p>Sign in</p>
		</div>
		<div class="login-box login-form bottom10">
			${error}
			<form method="post" action="">
				<input type="hidden" name="login" value="1" />
				<input type="text" class="w100p" placeholder="email" name="email">
				<div class="block right secondary">
					<a href="password_reset" tabindex="10">Forgot password?</a>
				</div>
				<input type="password" class="w100p" placeholder="password" name="pwd">
				<div class="block secondary left">
					<input type="checkbox" name="remember_me" value="1">
					Remember me
				</div>
				<button class="w100p">Sign in</button>
			</form>
		</div>
		<div class="secondary">
			New to feedrdr.co?<br> <a href="${baseUrlLink}">create an account</a>
			<p>
			</p>
		</div>
	</div>
	<script>
		$(document).ready(function() {
			$("form input:visible").first().focus()
		})
	</script>
</body>
</html>
