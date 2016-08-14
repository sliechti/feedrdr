function closeMsg(caller) {
	$(caller).closest('.msg').hide();
}

function clearMsgElem(elem) {
	elem.removeClass('msg-error msg-info msg-warning');
}

function showErrorMsg(elemId, msg) {
	var elem = $(elemId);
	elem.hide();
	clearMsgElem(elem);
	elem.addClass('msg-error');
	elem.find('.text').html(msg);
	elem.show();
}
