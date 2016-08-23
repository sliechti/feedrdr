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

	<c:choose>
		<c:when test="${not empty info}">
			<c:set var="msgClass" value="msg-info" />
			<c:set var="hideClass" value="" />
			<c:set var="msg" value="${info}" />
		</c:when>
		<c:when test="${not empty err}">
			<c:set var="msgClass" value="msg-error" />
			<c:set var="hideClass" value="" />
			<c:set var="msg" value="${err}" />
		</c:when>
		<c:otherwise>
			<c:set var="msgClass" value="" />
			<c:set var="hideClass" value="hide" />
		</c:otherwise>
	</c:choose>
	<div id="settings-msg" class="${hideClass} msg ${msgClass}">
		<div class="actions">
			<a href="#" onclick="closeMsg(this)">
				<i class="fa fa-close"></i>
			</a>
		</div>
		<p class="text">${msg}</p>
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