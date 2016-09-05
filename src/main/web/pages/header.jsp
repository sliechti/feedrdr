<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/security.jsp" %>
<c:set var="inReader" value="${pageContext.request.requestURI.contains('/reader')}" scope="request" />
<!DOCTYPE html>
<html>
<head>
<title>${param.title}</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/default.css" />
<script src="${baseUrl}/js/vendor/jquery.min.js"></script>
<script src="${baseUrl}/js/vendor/hammer.min.js"></script>
<script src="${baseUrl}/js/vendor/handlebars.min.js"></script>
<script src="${baseUrl}/js/vendor/jscolor/jscolor.min.js"></script>
<script src="${baseUrl}/js/vendor/director.min.js"></script>
<script src="${baseUrl}/js/vendor/jlinq.min.js"></script>
<script src="${baseUrl}/js/app.legacy.js"></script>
<script src="${baseUrl}/js/app.js"></script>
<script>
	setBaseUrl('${baseUrl}');
</script>
</head>
<body class="margin0">
	<div class="header bg-white primary-t-color font-large">
		<div id="header-input" class="hide center content-width">
			<c:if test="${showSearch}">
				<input type="text" id="search" class="header-line left"
					 placeholder="search"
					onkeypress="search()">
			</c:if>
			<a href="#" onclick="hideSearch();">
				<i class="fa fa-close fade-color right" aria-hidden="true"></i>
			</a>
		</div>
		<div id="header-content" class="center content-width">
			<div class="header-left">
				<c:choose>
					<c:when test="${showBackButton}">
						<a href="${baseUrl}/reader">
							<i class="fa fa-arrow-left"></i> Back
						</a>
					</c:when>
					<c:otherwise>
						<a href="#" onclick="openLeftBar()">
							<img src="${baseUrl}/img/app-logo.svg" height="40" id="app-logo" class="vertical-middle" />
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="header-right">
				<c:if test="${empty hideEllipsis}">
					<a href="#" class="right relative" onclick="showEl(this, 'header-el')">
						<i class="fa fa-ellipsis-v fade-color" aria-hidden="true"></i>
					</a>
				</c:if>
				<div id="header-el" class="el-menu el-menu-header">
					<ul>
						<c:if test="${showSettingsInMenu}">
							<li class="pointer" onclick="location.href='${baseUrl}/settings'">
								<a href="${baseUrl}/settings">
								Settings <i class="fa fa-cog"></i>
								</a>
							</li>
						</c:if>
						<li class="pointer" onclick="location.href='${baseUrl}/logout'">
							<a href="${baseUrl}/logout">
								Sign out <i class="fa fa-sign-out"></i>
							</a>
						</li>
					</ul>
				</div>
				<c:if test="${showSearch}">
					<a href="#" class="right" onclick="showSearch(this)">
						<i class="fa fa-search fade-color" aria-hidden="true"></i>
					</a>
				</c:if>
			</div>
		</div>
	</div>

<c:if test="${empty hideLeftMenu}">
	<jsp:include page="left-menu.jsp" />
</c:if>
