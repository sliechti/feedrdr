<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="mock-stream-data.jsp" %>

<div id="stream-list" class="line-view">
	<div id="line-view-options-tmpl" class="options hide">
		<i class="fa fa-bookmark-o"></i>
		<i class="fa fa-share"></i>
		<i class="fa fa-tag"></i>
		<i class="fa fa-info-circle"></i>
	</div>
	<c:forEach items="${streamEntries}" var="entry">
		<div id="article-${entry.id}" class="article" >
			<div class="angle" onclick="slLineOptions(this, '${entry.id}')">
				<i class="pointer fa fa-angle-right fade-color"></i>
			</div>
			<div class="content">
				<a href="http://feedrdr.co">${entry.title}</a>
			</div>
		</div>
	</c:forEach>
</div>
