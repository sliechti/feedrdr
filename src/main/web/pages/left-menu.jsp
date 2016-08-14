<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="left-menu" class="hide">

	<div class="header menu-header">
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

		<div id="profiles" class="hide bottom-shadow bg-white primary-t-color pr20p">
			<div class="no-overflow">
				<a class="right" href="${baseUrl}/settings/profiles">
					<i class="fa fa-cog"></i>
				</a>
			</div>
			<div class="content">

			</div>
		</div>

		<c:choose>
			<c:when test="${not inReader}">
				<div class="back">
					<a href="${baseUrl}/${readerUrl}">
						<i class="fa  fa-arrow-left">
							back to reader
						</i>
					</a>
				</div>
			</c:when>
			<c:otherwise>
			<div class="ul-section" id="special-entries">
			<ul>
				<li>
					<a href="#/v/a" id="mAll" onclick="closeLeftBar();" >All</a>
				</li>
				<li>
					<a href="#/v/s" id="mSaved" onclick="closeLeftBar();" >Saved</a>
				</li>
				<li>
					<a href="#/v/r" id="mRr" onclick="closeLeftBar();" >Recently read</a>
				</li>
			</ul>
			</div>
			<hr>
			<div class="ul-section" id="discover-entries">
				<ul>
					<li>
						<a href="${baseUrl}/collections">Collections</a>
					</li>
				</ul>
			</div>
			<hr>

	    <div id="streams-header" class="margin">
			<div id="streams-actions" class="ls-med menu-icons r20p">
				<a href="#" onclick="lmShowFilters()">
					<span class="left pl20p">STREAMS</span>
					<i class="fa fa-angle-down fade-color" aria-hidden="true"></i>
				</a>
	            <a href="#" onclick="lmShowAddContent()">
	            	<i id="plus-icon" class="fa fa-plus"></i>
	            </a>
	        </div>
	        <div>
				<div id="add-content" class="wide-padding border-bottom-fade hide">
					<div class="no-overflow">
						<input class="w80p left" type="text" id="stream-name"
							placeholder="New stream group" />
						<input class="w20p right" type="button" value="add"
							onclick="saveStreamGroup('stream-name')" />
					</div>
					<div id="stream-name-msg" class="msg hide"></div>
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
						<i class="fa fa-sort-amount-asc"></i>
					</a>
				</div>
	        </div>
	    </div>

		<div id='menusubs'></div>

	    <hr>

		<div id="left-add-content">
			<div class="box" onclick="addContent()">Add content</div>
		</div>

			</c:otherwise>
		</c:choose>

	</div>
</div>

<jsp:include page="tmpl/profiles.tmpl.jsp" />

