function hideSearch() {
	console.debug('hide search');
	var hi = $('#header-input');
	var hc = $('#header-content');
	hc.show();
	hi.addClass('hide');
}
function showSearch(caller) {
	console.debug('show search');
	var hi = $('#header-input');
	var hc = $('#header-content');
	hc.hide();
	hi.removeClass('hide');
	console.debug('header-input', hi);
	hi.show();
	var inp = hi.find('input');
	inp.focus();
}
