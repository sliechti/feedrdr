
function toTop() {
	window.scrollTo(0, 0);
}

function slideInLeft(selector, duration, onFinished) {
	var opt = $(selector);
	opt.css('left', opt.parent().width() * -1);
	opt.show();
	opt.animate({
		left: 0
	}, {
		complete: onFinished,
		duration: duration
	});
}

function slideOutLeft(selector, duration, onFinished) {
	var opt = $(selector);
	opt.animate({
		left: opt.parent().width() * -1
	}, {
		complete: onFinished,
		duration: duration
	});
}

function switchAngleLr(elem, from) {
	var to = (from == 'right') ? 'left' : 'right';
	elem.removeClass('fa-angle-' + from).addClass('fa-angle-' + to);
}

function showEl(id) {
	var el = $('#' + id);
	var w = el.width();
	el.toggle();

	$(document).on('mousedown.el' + id, function(e) {
		console.debug("mousedown", e);
		if (!el.is(e.target) && el.has(e.target).length == 0) {
			el.hide();
			$(document).off('mousedown.el' + id);
		}
	});
}

function addContent() {
	location.href = baseUrl + '/add';
}