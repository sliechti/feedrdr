
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

function showEl(caller, id) {
	var el = $('#' + id);
	if (!el.is(':visible')) {
		var w = el.width();
		el.show();
		$(document).on('mousedown.el', function(e) {
			if ($(caller).is(e.target) || $(caller).has(e.target).length > 0) {
				return;
			} else if (!el.is(e.target) && el.has(e.target).length == 0) {
				el.hide();
				$(document).off('mousedown.el');
			}
		});
	} else {
		el.hide();
		$(document).off('mousedown.el');
	}
}

function addContent() {
	var to = selectedStream.l_stream_id;
	var append = (to) ? '?to=' + to : '';
	location.href = baseUrl + '/add' + append;
}


/**
 * @param pObj jquery object
 */
function attachColorPicker(pObj) {
	var picker = new jscolor.color(pObj.get(0), {
		slider : true,
		pickerFaceColor : 'transparent',
		pickerMode : 'HSV',
		pickerFace : 1,
		pickerBorder : 0,
		pickerInsetColor : 'black'
	});
	picker.fromString(pObj.attr('data-color'));
}
