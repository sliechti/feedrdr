<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="header.jsp">
	<jsp:param value="Add Content" name="title" />
</jsp:include>


<div class="center content-width">
	<div id="add-content" class="mt20p">

	<c:if test="${not empty handler}">
		<div class="msg msg-info">
			<b>Import status</b><br>
			Subscriptions imported: ${handler.subsOk}<br>
			Subscriptions that couldn't be imported: ${handler.subsErrors}<br>
			Folders found: ${handler.streamsOk}<br>
			Folders that failed to be processed: ${handler.streamsErrors}<br>
			New sources found and queued to be processed: ${handler.sourceQueued}<br>
			Sources known and ready to read: ${handler.sourceKnown}<br>
			Sources that couldn't be imported: ${handler.sourceError}<br>
		</div>
	</c:if>
	<div id="add-msg" class="hide msg msg-info">
		<div class="actions">
			<a href="#" onclick="closeMsg(this)">
				<i class="fa fa-close"></i>
			</a>
		</div>
		<p class="text">MESSAGE </p>
	</div>
	<c:if test="${not empty error}">
		<div class="msg msg-error">
			${error}
		</div>
	</c:if>
		<div class="font-med">
			Feeds will be added to the current profile <b>${profile.name}</b>
		</div>
		<hr class="hr-separator">
		<c:if test="${not empty param.to}">
			<div id="import-url" class="form form-wide">
				<div>
					<input type="text" id="source-url" placeholder="http://domain.com/feed">
				</div>
				<div>
					<input onclick="addSubscribeUrl('source-url', ${param.to}); return false;"
						type="submit" value="Add feed to '${togroup.name}'">
				</div>
			</div>
		</c:if>
		<hr class="hr-separator">
		<div id="import-opml" class="form form-wide">
			<div id="opml" class="col-xs-12 form-group">
				<form method="POST" action="" enctype="multipart/form-data">
					<div>
						<p>Import feeds from an OPML file</p>
					</div>
					<div>
						<input type="file" name="opml-file" value="Select OPML file">
					</div>
					<div>
						<input type="submit" class="" name="import_opml" value="Import OPML file">
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
