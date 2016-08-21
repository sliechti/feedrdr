<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="header.jsp">
	<jsp:param value="header" name="title" />
</jsp:include>

<div id="settings-header" class="sub-header">
	<div class="content center content-width">
		<div class="settings-header">
			<ul class="header-tabs">
				<li>
					<a href="${baseUrl}/settings/account">
						Account
					</a>
				</li>
				<li>
					<a href="${baseUrl}/settings/profiles">
						Profiles
					</a>
				</li>
				<li>
					<a href="${baseUrl}/settings/notifications">
						Notifications
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>


<div class="center content-width">
<div id="settings-content">

	<div id="settings-msg" class="hide msg msg-error">
		<div class="actions">
			<a href="#" onclick="closeMsg(this)">
				<i class="fa fa-close"></i>
			</a>
		</div>
		<p class="text"></p>
	</div>

<c:choose>
	<c:when test="${fn:endsWith(fwdURI, '/profiles')}">
		<jsp:include page="view/settings/settings-profiles-view.jsp" />
		<jsp:include page="view/settings/settings-handlebars.jsp" />
	</c:when>
	<c:when test="${fn:endsWith(fwdURI, '/notifications')}">
		<jsp:include page="view/settings/settings-notifications-view.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="view/settings/settings-account-view.jsp" />
	</c:otherwise>
</c:choose>

</div>
</div>

<jsp:include page="footer.jsp" />