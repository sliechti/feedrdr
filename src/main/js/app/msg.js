function closeMsg(caller) {
	$(caller).closest('.msg').hide();
}

function clearMsgElem(elem) {
	elem.removeClass('msg-error msg-info msg-warning');
}

function showErrorMsg(elemId, msg) {
	showMsg(elemId, msg, 'msg-info');
}

function showInfoMsg(elemId, msg) {
	showMsg(elemId, msg, 'msg-info');
}

function showMsg(elemId, msg, clzz) {
	var elem = $(elemId);
	elem.hide();
	clearMsgElem(elem);
	elem.addClass(clzz);
	elem.find('.text').html(msg);
	elem.show();
}