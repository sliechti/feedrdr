<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${language}" scope="session" />
<fmt:setBundle basename="i18n.message" />

<jsp:include page="header.jsp">
	<jsp:param name="title" value="An open source RSS news reader" />
</jsp:include>

<div class="center content-width content">
	<div id="center-piece">
		<div class="cell w50p">
			<p class="intro">
			<fmt:message key="welcome.intro"/>
			</p>
		</div>
		<div class="cell text-center w50p vertical-middle bottom40">
			<c:if test="${not empty invalidEmail}">
				<div class="warning">
				The email '${email}' appears to be invalid, please try another one.
				</div>
			</c:if>
			<c:if test="${not empty emailKnown}">
				<div class="warning">
				The email is already registered, please <a href="login">sign in</a>
				or <a href="password_reset">reset you password</a>
				</div>
			</c:if>
			<form id="start-reading" method="post" action="signup">
				<input type="text" class="block w100p" id="email" name="email" placeholder="your@email">
				<button class="block w100p btn">Start reading</button>
			</form>
			<p class="lead">Or</p>
			<p class="lead">
				<a class="btn-auth btn-facebook" href="#" onclick="hello('facebook').login({force: true, scope: 'public_profile,email'});return false;">
				<i class="fa fa-facebook r20p" aria-hidden="true"></i></a>
				<a class="btn-auth btn-google" href="#" onclick="hello('google').login({force : true, scope: 'profile email'});return false;">
				<i class="fa fa-google r20p" aria-hidden="true"></i></a>
				<a class="btn-auth btn-windows" href="#" onclick="hello('windows', {scope: 'wl.emails,wl.basic'}).login();return false;">
				<i class="fa fa-windows r20p" aria-hidden="true"></i></a>
			</p>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />
