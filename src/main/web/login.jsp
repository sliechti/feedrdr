<!DOCTYPE html>
<html>
<head>
<title>Login</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<link rel="stylesheet" href="${baseUrl}/css/login.css" />
</head>
<body>
	<div class="login">
		<div class="login-header">
			<p>
			Logo
			</p>
			<p>Sign in</p>
		</div>
		<div class="login-box login-form bottom10">
			${error}
			<form method="post" action="">
				<input type="hidden" name="login" value="1" /> <input type="text" class="w100p" placeholder="email" name="email"> <input
					type="password" class="w100p" name="pwd">
				<button class="w100p">Sign in</button>
			</form>
		</div>
		<div class="login-footer">
			New to feedrdr.co?<br> <a href="${baseUrl}">create an account</a>
			<p>
				<a href="forgot">Forgot my password</a>
			</p>
		</div>
	</div>
</body>
</html>
