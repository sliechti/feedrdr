<jsp:include page="header.jsp" />

<div class="content">
<div class="center content-width font-large">

	<div id="500">
		<c:choose>
			<c:when test="${isLocal}">
				<h3>${pageContext.exception.message}</h3>
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
</div>

<jsp:include page="footer.jsp" />
