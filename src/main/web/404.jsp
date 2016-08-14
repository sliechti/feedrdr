<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="404 - page not found" />
</jsp:include>

<div class="content">
<div class="center content-width font-large">
	<div id="404">
		<p class="text-center">Page not found</p>
		<hr class="hr-separator">
		<p class="text-center">
		Please <a href="${baseUrl}/login">sign in</a>
		or <a href="${baseUrl}/signup">sign up</a></p>
	</div>
</div>
</div>

<jsp:include page="footer.jsp" />
