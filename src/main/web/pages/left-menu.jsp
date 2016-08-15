<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="inReader" value="${pageContext.request.requestURI.contains('/reader')}" />

<div id="left-menu" class="hide">

	<div class="header font-med menu-header">
		<div id="header-content" class="pl20p pr20p ">
			<div class="left max300">
				<a href="#" class="decor-none upper" onclick="lmToggleProfiles()">
					<i id="header-p-angle" class="profile-color fa fa-user v-middle"></i>
				</a>
			</div>
			<div class="right">
				<a href="#" class="right" onclick="closeLeftBar()">
					<i class="fa fa-close" aria-hidden="true"></i>
				</a>
			</div>
		</div>
	</div>

	<div id="left-menu-content" class="primary-t-color">

		<div id="profiles" class="hide font-med bottom-shadow bg-white primary-t-color">
			<div class="content">

			</div>
			<div class="border-tb-fade">
				<a href="settings.jsp">
					New Profile
				</a>
			</div>
		</div>

		<c:if test="${not inReader}">
			<div>
				<a href="${baseUrl}/${readerUrl}">back to reader</a>
			</div>
		</c:if>

	    <div class="ul-section" id="special-entries">
	    <ul>
	    	<li>
		        <a href="#/v/a" id="mAll" onclick="closeLeftBar();" style="display: block">All</a>
	    	</li>
	    	<li>
		        <a href="#/v/s" id="mSaved" onclick="closeLeftBar();" style="display: block">Saved</a>
			</li>
			<li>
				<a href="#/v/r" id="mRr" onclick="closeLeftBar();" style="display: block">Recently read</a>
			</li>
<!-- 			<li> -->
<%-- 				<a href="${readerUrl}#/v/r" id="mRr" onclick="closeLeftBar();" style="display: block">Discover</a> --%>
<!-- 			</li> -->
<!-- 			<li> -->
<%-- 				<a href="${readerUrl}#/v/r" id="mRr" onclick="closeLeftBar();" style="display: block">Trending</a> --%>
<!-- 			</li> -->
	    </ul>
	    </div>
	    <hr>

		<c:if test="${inReader}">

	    <div id="streams-header" class="font-med">
			<div id="streams-actions" class="ls-med menu-icons r20p">
				<span class="left pl20p">STREAMS</span>
				<a href="#" onclick="lmShowFilters()">
					<i class="fa fa-angle-down fade-color" aria-hidden="true"></i>
				</a>
<!-- 	            <a href="#" onclick="lmShowFilters()"> -->
<!-- 					<i id="filter-icon" class="fa fa-filter"></i> -->
<!-- 				</a> -->
<!-- 	            <a href="#" onclick="lmShowAddContent()"> -->
<!-- 	            	<i id="plus-icon" class="fa fa-plus"></i> -->
<!-- 	            </a> -->
	        </div>
	        <div>
				<div id="add-content" class="border-bottom-fade hide">
					<ul class="leftmenu-ul">
						<li>
							<a href="${baseUrl}/pages/collections.jsp">Add new collections</a></li>
						<li>
							<a onclick="closeLeftBar();" href="${baseUrl}/pages/import.jsp" style="display: block">Import feeds</a>
						</li>
						<li>
							<a onclick="closeLeftBar();" href="${baseUrl}/pages/subscriptions.jsp" style="display: block">Manage subscriptions</a>
						</li>
						<li>
							<a href="" onclick="newStreamGroup(); return false;">From URL</a>
						</li>
					</ul>
				</div>
				<div id="streams-filter" class="ls-med border-bottom-fade menu-icons hide">
					<a title="show all" href="" onclick="showOnlyWithUnread(false); return false;">
						<i class="fa fa-eye"></i>
					</a>
					<a title="show only unread" href="" onclick="showOnlyWithUnread(true); return false;">
						<i class="fa fa-eye-slash"></i>
					</a>
					<a title="sort A-Z" href="" onclick="sortByAlphabet(2);return false;">
						<i class="fa fa-sort-alpha-asc"></i>
					</a>
					<a title="sort Z-A" href="" onclick="sortByAlphabet(1);return false;">
						<i class="fa fa-sort-alpha-desc"></i>
					</a>
					<a title="sort by unread 9-0" href="" onclick="sortByUnread(1);return false;">
						<i class="fa fa-sort-amount-desc"></i>
					</a>
					<a title="sort by unread 0-9" href="" onclick="sortByUnread(2);return false;">
						<i class="fa fa-sort-amount-desc"></i>
					</a>
				</div>
	        </div>
	    </div>

		<div id='menusubs'></div>

		</c:if>

	    <hr>

		<div id="left-add-content">
			<div class="box" onclick="addContent()">Add content</div>
		</div>
	</div>
</div>

<jsp:include page="tmpl/profiles.tmpl.jsp" />

