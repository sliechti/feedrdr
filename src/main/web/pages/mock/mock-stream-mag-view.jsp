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
		<div id="news_${entry.id}" class="news-mag">
			<div class="content">
				<div class="img-wrap">
					<img style="background: transparent url(&quot;http://i.amz.mshcdn.com/C0wO4JX5GJn6iNx8pRAT4UuNBDE=/720x480/https%3A%2F%2Fblueprint-api-production.s3.amazonaws.com%2Fuploads%2Fstory%2Fthumbnail%2F17180%2Frefugee.jpg&quot;) no-repeat scroll center center"
					class="left margin10" id="img_637157232" src="/img/1px.png">
				</div>
				<a class="title" href="https://feedrdr.co">
					${entry.title}
				</a>
				<p class="content" id="cnt_637157232">
					The Refugee Olympic Team received a roaring welcome when
					it formally entered the Olympic arena on Friday night.
					The crowd erupted into cheers as the team preceded the hosts
					into Maracan Stadium, but love for the historic 10 extends
					far beyond Rio. SEE ALSO: Refugee team receives incredible
					welcome at the Olympics Opening Ceremony Over in Kenya,
					home to the host National Olympic Committee for several of
					the athletes, special screenings of the ceremony and the
					subsequent events have been set up.
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
