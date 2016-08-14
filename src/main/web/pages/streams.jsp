<div id="stream-header" class="sub-header sub-line">
	<div class="content center content-width">
		<div class="stream-header">
			<div class="angle" onclick="shToggleActions(this)">
				<i class="pointer fa fa-angle-right"></i>
			</div>
			<div class="stream-title title">
				Technology
				<div id="stream-actions" class="hide">
					<i class="fa fa-check-circle"></i>
					<i class="fa fa-plus-circle"></i>
				</div>
				<div class="right">
					<i onclick="shToggleSettings()"
						class="-icon-hide pointer fa fa-cog right fade-color"></i>
				</div>
			</div>
		</div>
		<div id="stream-options" class="hide">
			<i class="fa fa-remove"></i>
			<i class="fa fa-share"></i>
		stream options
		</div>
	</div>
</div>

<div class="center content-width">
<div id="stream-content">
<%-- 	<jsp:include page="mock/mock-stream-line-view.jsp" /> --%>
	<jsp:include page="mock/mock-stream-mag-view.jsp" />
<%-- 	<jsp:include page="mock/mock-stream-card-view.jsp" /> --%>
	<jsp:include page="stream-footer.jsp" />
</div>
</div>

