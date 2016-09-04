<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp">
	<jsp:param name="title" value="reader" />
</jsp:include>

<jsp:include page="tmpl/collections.tmpl.jsp"></jsp:include>

<div class="center content-width">
<div id="collections-content">
<div class="wrapper">
</div>
</div>
</div>

<script type="text/javascript">
	initCollections();
</script>

<jsp:include page="footer.jsp"></jsp:include>
