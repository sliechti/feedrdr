<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Password reset</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="${baseUrl}/js/jquery${minifiedStr}.js" type="text/javascript" /></script>
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
			<c:if test="${not empty err}">
				<div class="warn-msg">${err}</div>
			</c:if>
			<c:if test="${not empty info}">
				<div class="info-msg">${info}</div>
			</c:if>
			<c:if test="${not empty pwdChanged}">
				<div> Go to <a href="login">login</a> </div>
			</c:if>
			<c:choose>
				<c:when test="${validCode}">
					<form method="post" action="password_reset">
						<input type="hidden" name="code" value="${param.code}">
						<input type="hidden" name="submit" value="1">
						<input type="password" class="w100p" name="pwd1" value="" placeholder="password">
						<input type="password" class="w100p" name="pwd2" value="" placeholder="confirm password">
						<button class="w100p">Reset password</button>
					</form>
				</c:when>
				<c:when test="${not empty pwdChanged}">
				</c:when>
				<c:otherwise>
					<form method="post" action="password_reset">
						<p>
							Please enter your email below. We will send you a link to reset
							your password.
						</p>
						<input type="hidden" name="reset" value="1" />
						<input type="text" class="w100p" placeholder="email" id="email" name="email">
						<button class="w100p">Send email</button>
					</form>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="secondary">
			<p>New to feedrdr.co?<br> <a href="${baseUrlLink}">create an account</a><br>
				or<br>
			<a href="login">Sign in</a></p>
		</div>
	</div>
<script>
	$(document).ready(function() {
		$("form input:visible:first").focus();
	});
</script>
</body>
</html>
