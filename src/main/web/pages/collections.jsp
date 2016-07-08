<%@page import="feedreader.config.FeedAppConfig"%>

<% request.setAttribute("c", true); %>

<%@include file="header.jsp" %>
<jsp:include page="tmpl/collections.tmpl.jsp"></jsp:include>

<div class="col-xs-12">
<h4>A couple of collections created by us to get you started.</h4>
</div>

<div class="row">
	<div class="col-xs-12 col-sm-6 col-md-4" id="col0"></div>
	<div class="col-xs-12 col-sm-6 col-md-4" id="col1"></div>
	<div class="col-xs-12 col-sm-6 col-md-4" id="col2"></div>
</div>

<script type="text/javascript">
	initCollections();
</script>

<jsp:include page="footer.jsp"></jsp:include>
