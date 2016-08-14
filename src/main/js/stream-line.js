function slLineOptions(caller, id) {
	var content = $('#article-' + id).find('.content');
	var ielem = $(caller).find('i');
	if (ielem.hasClass('fa-angle-right')) {
		if ($('options-' + id).length == 0) {
			var tmpl = $('#line-view-options-tmpl').clone();
			tmpl.first().attr('id', 'options-' + id);
			tmpl.first().attr('data-id', id);
			content.append(tmpl);
		}
		slideInLeft('#options-' + id, 250, function() {
			switchAngleLr(ielem, 'right');
		});
	}  else {
		slideOutLeft('#options-' + id, 250, function() {
			switchAngleLr(ielem, 'left');
		});
	}
}

function slCloseOptions(caller) {
	var elem = $(caller).closest('div .options');
	var id = elem.attr('data-id');
	var news = $('#article-' + id);
	slOutRight('options-' + id, news.width());
}