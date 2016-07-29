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
				<a href="login">Sign in</a>
			</div>
		</div>
	</div>
	<div class="content">
		<div class="center w80p">
			<c:choose>
				<c:when test="${isLocal}">
					<c:forEach var="trace" items="${pageContext.exception.stackTrace}">
						${trace}<br>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<p>There was an error in the page you requested.
					<br>
					<br>
					We are informed in multiple ways when this happens,
					so rest assured we will try to fix it as soon as possible.</p>

					<p>In the meanwhile, try <a href="#" onclick="window.history.back();">going back</a>
					or </a><a href="${baseUrl}/login">signing in</a> again</p>

				</c:otherwise>
			</c:choose>
		</div>
	</div>
</body>
</html>
