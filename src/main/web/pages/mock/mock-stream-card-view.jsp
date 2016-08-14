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
		<div id="news_637191037" class="news-card">
			<div class="img-wrap" id="img_637191037">
				<img src="http://img-2.gizmag.com/prospector-one-2.jpg?auto=format%2Ccompress&amp;fit=max&amp;h=297&amp;q=60&amp;rect=0%2C45%2C1500%2C844&amp;w=530&amp;s=b67382140af0e0f8084cbc2166fd4564">
			</div>
			<div class="content">
				<a name="link" class="title" data-id="637191037" href="https://feedrdr.co" target="_blank">
					${entry.title}
				</a>
				<p>
					Deep Space Industries (DSI) today announced that it will conduct what it claims is the
					world's first commercial interplanetary mining mission. In conjunction with the government of Luxembourg, the Silicon Valley-based
					company is planning to launch an unmanned spacecraft called Prospector-1 to intercept, survey, and land on a near-Earth asteroid as a
					prelude to space mining operations... Continue Reading Deep Space Industries plans first private asteroid landing as mining prelude
					Category: Space
				</p>
			</div>
			<div class="news-footer">
				<div class="actions">
					<i class="fa fa-bookmark-o"></i>
					<i class="fa fa-share"></i>
					<i class="fa fa-tag"></i>
					<i class="fa fa-info-circle"></i>
				</div>
				<div class="right source">
					<a href="#">
						mashable
					</a>
				</div>
			</div>
		</div>
	</c:forEach>
</div>
