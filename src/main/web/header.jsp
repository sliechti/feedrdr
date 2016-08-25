<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${language}" scope="session" />
<fmt:setBundle basename="i18n.message" />

<!DOCTYPE html>
<html lang="${language}">
<head>
<title>${param.title}</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="description" content="open source rss news reader to keep track of all the content you care about" />
<meta name="keywords" content="rss,atom,news reader,feed reader" />
<meta name="google-site-verification" content="w2qoFGms5i-1odj9Jj2G3CTCtMokK79Oi93mwglzNkk" />
<meta name="msvalidate.01" content="13BFF01091BF323BC721335417DC72C4" />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-title" content="feedrdr.co">
<meta name="application-name" content="feedrdr.co">
<link rel="apple-touch-startup-image" href="startup.png">
<link rel="apple-touch-icon" href="img/app-196x196.png"/>
<link rel="apple-touch-icon-precomposed" sizes="128x128" href="img/app-128x128.png">
<link rel="shortcut icon" sizes="196x196" href="img/app-196x196.png">
<link rel="shortcut icon" sizes="128x128" href="img/app-128x128.png">
<link rel="shortcut icon" href="${baseUrl}/favicon.ico" type="image/x-icon">
<link rel="icon" href="${baseUrl}/favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/welcome.css" />
<script src="${baseUrl}/js/vendor/jquery.min.js" type="text/javascript" /></script>
<script src="${baseUrl}/js/vendor/hello.min.js" type="text/javascript"></script>
<script src="${baseUrl}/js/vendor/hello.init.min.js" type="text/javascript"></script>
<c:if test="${not isLocal}">
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
</c:if>
</head>
<body class="margin0">
	<div class="header">
		<div class="table center content-width block">
			<div class="cell left h100">
				<span class="h100 vertical-helper vertical-middle"></span>
				<img src="${baseUrl}/img/logo.svg" id="logo" class="vertical-middle" height="30" />
			</div>
			<div onclick="location.href='login'" class="pointer cell vertical-middle right header-btn">
				<a href="login"><fmt:message key="welcome.signin"/></a>
			</div>
		</div>
	</div>
