
function addSubscribeUrl(elemId, streamId) {
	var queryData = {};
	queryData.n = '';
	queryData.sid = streamId;
	queryData.u = $('#' + elemId).val();
	addSubscription(queryData, function(data) {
		console.debug('add subscribe response', data);
		if (data.success) {
			showInfoMsg('#add-msg', data.success);
			$('#' + elemId).val('');
		} else if (data.error) {
			showErrorMsg('#add-msg', data.error);
		} else {
			showErrorMsg('#add-msg', 'unknown response: ' + JSON.stringify(data));
		}
	});
}
