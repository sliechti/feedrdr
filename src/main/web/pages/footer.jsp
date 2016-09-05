<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty showSearch}">
	<jsp:include page="tmpl/search.tmpl.jsp" />
</c:if>
<script>
    initProfiles();

    registerOnProfilesAvailable(function() {
        selectProfile(${profile.profileId});
    });

    $(document).ready(function() {
        $("#footer_spacing").css("height", screen.height - 100);
        $(document).scroll( function() {
        	$(window).scrollTop(0);
        	$(document).unbind("scroll");
        });
    });
</script>


</body>
</html>
