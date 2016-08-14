
function saveStreamGroup(elemId) {

	var input = $("#" + elemId);
	raClearMsg(elemId);

	var queryData = {};
	queryData.sn = input.val();
	if (input.val().length == 0) {
		raError(elemId, "Please enter a name");
		return;
	}
	$.get(baseUrl + apiUrlStreamsAdd, queryData, function(data) {
		if (data.success) {
			getStreamGroups(function() {
				loadStream(data.id);
				closeLeftBar();
			});
		} else {
			alert(data.error);
		}
	}, "json");
}

function raClearMsg(elemId) {
	var e = $("#" + elemId + "-msg");
	e.removeClass('msg-error');
	e.hide();
	e.html('');
}

function raError(elemId, msg) {
	var e = $("#" + elemId + "-msg");
	e.html(msg);
	e.addClass('msg-error');
	e.show();
}