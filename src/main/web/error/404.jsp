<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>404 Error</title>
<meta charset="utf-8">
<link rel="icon" href="${baseUrl}/favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>
<body class="margin0">
	<div class="header">
		<div class="table center w80p block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span>
				<img src="${baseUrl}/img/logo.svg" id="logo" class="vertical-middle" height="30" />
			</div>
			<div onclick="location.href='login'" class="pointer cell vertical-middle right header-btn">
				<a href="${baseUrl}/login">Sign in</a>
			</div>
		</div>
	</div>
	<div class="content">
		<div class="center w80p">
			<p class="text-center">Page not found.</p>
			<hr>
			<p class="text-center">
			Please <a href="${baseUrl}/login">sign in</a>
			or <a href="${baseUrl}/signup">sign up</a></p>
		</div>
	</div>
</body>
</html>
