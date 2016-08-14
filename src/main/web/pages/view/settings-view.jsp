<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="settings-header" class="sub-header">
	<div class="content center content-width">
		<div class="settings-header">
			<ul class="header-tabs">
				<li>
					<a href="">
						Account
					</a>
				</li>
				<li>
					<a href="">
						Profiles
					</a>
				</li>
				<li>
					<a href="">
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

	<jsp:include page="settings/settings-handlebars.jsp" />

<%-- 	<jsp:include page="settings/settings-account-view.jsp" /> --%>
	<jsp:include page="settings/settings-profiles-view.jsp" />
<%-- 	<jsp:include page="settings/settings-profile-view.jsp" /> --%>
</div>
</div>
