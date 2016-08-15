
<%@include file="/security.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>${param.title}</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="${baseUrl}/css/font-awesome.min.css" />
<link rel="stylesheet" href="${baseUrl}/css/default.css" />
<script src="${baseUrl}/js/vendor/jquery.min.js"></script>
<script src="${baseUrl}/js/vendor/handlebars.min.js"></script>
<script src="${baseUrl}/js/vendor/jscolor/jscolor.min.js"></script>
<script src="${baseUrl}/js/vendor/director.min.js"></script>
<script src="${baseUrl}/js/vendor/jlinq.min.js"></script>
<script src="${baseUrl}/js/app.legacy.js"></script>
<script src="${baseUrl}/js/app.js"></script>
</head>
<body class="margin0">
	<div class="header bg-white primary-t-color font-large">
		<div id="header-input" class="hide center content-width">
			<input type="text" class="header-line left" placeholder="search">
			<a href="#" onclick="hideSearch();">
				<i class="fa fa-close fade-color right" aria-hidden="true"></i>
			</a>
		</div>
		<div id="header-content" class="center content-width">
			<div class="header-left">
<!-- 				<i class="fa fa-bars"></i> -->
				<a href="#" class="vertical-middle" onclick="openLeftBar()">
					<img src="${baseUrl}/img/app-logo.svg" height="40" id="app-logo" class="vertical-middle" />
				</a>
			</div>
			<div class="header-right">
				<a href="#" class="right relative" onclick="showEl('header-el')">
					<i class="fa fa-ellipsis-v fade-color" aria-hidden="true"></i>
				</a>
				<div id="header-el" class="el-menu el-menu-header">
					<ul>
						<li>
							<a href="${baseUrl}/settings">
								Settings <i class="fa fa-cog"></i>
							</a>
						</li>
						<li>
							<a href="${baseUrl}/logout">
								Sign out <i class="fa fa-sign-out"></i>
							</a>
						</li>
					</ul>
				</div>
				<a href="#" class="right" onclick="showSearch(this)">
					<i class="fa fa-search fade-color" aria-hidden="true"></i>
				</a>
			</div>
		</div>
	</div>

	<jsp:include page="left-menu.jsp" />
