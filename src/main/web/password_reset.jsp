<!DOCTYPE html>
<html>
<head>
<title>Password reset</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<link rel="stylesheet" href="${baseUrl}/css/login.css" />
</head>
<body>
	<div class="login">
		<div class="login-header">
			<img src="img/app-logo.svg" height="100"/>
			<p>Reset your password</p>
		</div>
		<div class="login-box login-form bottom10">
			${error}
			<form method="post" action="">
				<p>
					Please enter your email below. We will send you a link to reset
					your password.
				</p>
				<input type="hidden" name="reset" value="1" />
				<input type="text" class="w100p" placeholder="email" name="email">
				<button class="w100p">Send email</button>
			</form>
		</div>
		<div class="secondary">
			New to feedrdr.co?<br> <a href="${baseUrlLink}">create an account</a>
			<p>
			</p>
		</div>
	</div>
</body>
</html>
