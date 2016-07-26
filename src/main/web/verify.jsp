<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Verify</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<script src="${baseUrl}/js/vendor/jquery.min.js" type="text/javascript" /></script>
</head>

<body class="margin0">
	<div class="header">
		<div class="table center w80p block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span> <img src="img/logo.svg" id="logo" class="vertical-middle" height="30" />
			</div>
			<div class="cell vertical-middle right header-btn">
				<a href="login">Sign in</a>
			</div>
		</div>
	</div>

	<div class="content">
		<div class="center w80p">
			<c:choose>
			<c:when test="${not empty verified}">
				<p class="text-center">
					Account verified<br>
					You will be redirected to the <a href="${baseUrl}/login">login page</a>
					 in <label id='seconds' class="bold">${redirectIn}</label> seconds
				</p>
			</c:when>
			<c:otherwise>
				<p class="text-center">${msg}</p>
				<form method="get" class="lined30" action="verify">
					<input class="w50p left" type="text" name="code" value="${code}" placeholder="Verification code">
					<input class="w50p right" type="submit" name="submit" value="verify">
				</form>
			</c:otherwise>
		</c:choose>

		</div>
	</div>

	<div class="footer">
		<ul>
			<li><a href="http://github.com/sliechti/feedrdr">GitHub</a></li>
			<li><a href="https://twitter.com/feedrdrco">Twitter</a></li>
			<li><a href="https://www.facebook.com/feedrdr">Facebook</a></li>
		</ul>
	</div>

</body>

<c:if test="${not empty verified}">
<script type="text/javascript">
	    var seconds = ${redirectIn};
	    function redirect() {
	        seconds--;
	        document.getElementById('seconds').innerHTML = seconds;
	        if (seconds <= 0) {
	            location.assign('${baseUrl}/login');
			}
			window.setTimeout(redirect, 1000);
		};
		window.setTimeout(redirect, 1000);
		document.getElementById('seconds').innerHTML = ${redirectIn};
</script>
</c:if>