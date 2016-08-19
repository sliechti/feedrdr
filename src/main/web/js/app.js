
$(document).on('keyup', function(e) {
	if (e.keyCode == 27) {
		openLeftBar();
	}
});
$(document).on('swiperight', function() {
	openLeftBar();
});




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

var leftMenuId = '#left-menu';
var streamsFilterId = '#streams-filter';
var addContentId = '#add-content';
var filterIconId = '#filter-icon';
var plusIconId = '#plus-icon';
var profilesId = '#profiles';

function closeAnimate() {
	var lm = $(leftMenuId);
	lm.animate({
		left : lm.width() * -1
	}, 400);
}

function openAnimate() {
	var lm = $(leftMenuId);
	lm.animate({
		left : 0
	}, 400);
}

function closeLeftBar() {
	closeProfiles();
	closeAnimate();
	$(document).off('keyup.leftbar');
	$(document).off('mousedown.leftbar');
	$(leftMenuId).off('swipeleft');
}

function openLeftBar() {
	$(leftMenuId).show();
	openAnimate();
	if ($(leftMenuId).is(':visible')) {
		$(document).on('keyup.leftbar', function(e) {
			if (e.keyCode == 27) {
				closeLeftBar();
			}
		});
		$(leftMenuId).on('swipeleft', function() {
			closeLeftBar();
		});
		$(document).on('mousedown.leftbar', function(e) {
			var container = $(leftMenuId);
			if (!container.is(e.target) && container.has(e.target).length === 0) {
				closeLeftBar();
			}
		});
	}
}

function lmToggleProfiles() {
	var p = $(profilesId);
	$(profilesId).toggle();
//	checkProfileAngle();
}

function closeProfiles() {
	$(profilesId).hide();
//	checkProfileAngle();
}

function checkProfileAngle() {
	var p = $(profilesId);
	var angle = $('#header-p-angle');
	if (p.is(":visible")) {
		angle.removeClass('fa-angle-down').addClass('fa-angle-up');
	} else {
		angle.removeClass('fa-angle-up').addClass('fa-angle-down');
	}
}

function lmShowAddContent() {
	$(plusIconId).addClass('fa-active');
	$(filterIconId).removeClass('fa-active');
	$(streamsFilterId).hide();
	$(addContentId).toggle();
	if ($(addContentId).is(':visible')) {
		$(plusIconId).addClass('fa-active');
	} else {
		$(plusIconId).removeClass('fa-active');
	}
}

function lmShowFilters() {
	$(plusIconId).removeClass('fa-active');
	$(addContentId).hide();
	$(streamsFilterId).toggle();
	if ($(streamsFilterId).is(':visible')) {
		$(filterIconId).addClass('fa-active');
	} else {
		$(filterIconId).removeClass('fa-active');
	}
}

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
var spTmpl = spTmpl || {};

function settingsError(msg) {
	showErrorMsg('#settings-msg', msg);
}

function settingsInfo(msg) {
	showErrorMsg('#settings-msg', msg);
}

function loadColorPickers() {
	$('.profile-row').find('input[data-color]').each(function(idx, o) {
		attachColorPicker($(o));
	});
}

function forwardSaveProfile() {
	var name = $('#name_profile_0');
	var color = $("#picker_profile_0");

	var queryData = {};
	queryData.n = name.val();
	queryData.c = color.val();

	$.getJSON(baseUrl + "/api/v1/user/profiles/new", queryData, function(data) {
		if (data.success) {
			setSessionProfile(data.profileid, function() {
				selectProfile(data.profileid);
			});
			hideModal();
		} else {
			settingsError(data.error);
		}
	});
}
function addNewProfile() {
	console.debug('add new profile');
	var tmpl = $(Handlebars.partials['tmplprofile'](
			{
				'l_profile_id': '0',
				's_profile_name': '',
				's_color': '#173C4F'
			}
		));
	var i = $('<i class="fa fa-plus"></i>').on('click', forwardSaveProfile);
	var actions = $(tmpl).find('.actions');
	actions.find('.fa-remove').hide()
	actions.append(i);

	$('#profile-list').append($(tmpl));
	attachColorPicker($('#picker_profile_0'));
	disableAddBox();
}

function disableAddBox() {
	$('#settings-profile').find('.box').addClass('disabled').off('click');
}

$(document).ready(function() {
	registerOnProfilesAvailable(function() {
		var tmplprofile = $('#tmpl-settings-profile').html();
		if (tmplprofile) {
			Handlebars.registerPartial('tmplprofile', tmplprofile);
			spTmpl = Handlebars.compile($('#tmpl-settings-profile-list').html());
			if (profiles) {
				$('#profile-list').html(spTmpl({
					'profiles' : profiles
				}));
				var sp = $('#settings-profile');
				sp.find('.box').on('click', addNewProfile);
			}
			loadColorPickers();
		}
	});
});

function sourceAdd() {
	alert('add-source');
}

$(document).ready(function() {
	$('.source-add').on('click', sourceAdd);
});
var streamActionsId = '#stream-actions';
var streamHeaderAngleId = '#stream-header-angle';
var streamOptionsId = '#stream-options';

function shToggleActions(caller) {
	var ielem = $(caller).find('i');
	console.debug(ielem);
	if (ielem.hasClass('fa-angle-right')) {
		slideInLeft(streamActionsId, 500, function() {
			switchAngleLr(ielem, 'right');
		});
	}  else {
		slideOutLeft(streamActionsId, 500, function() {
			switchAngleLr(ielem, 'left');
		});
	}
}

function shToggleSettings(caller) {
	var so = $(streamOptionsId);
	so.toggle();
	checkColor(streamOptionsId, streamHeaderAngleId);
}

function slForwardSaveEntry(caller) {
	var id = slGetDataId(caller);
	saveEntry(caller, id);
}

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

function slGetDataId(caller) {
	var elem = $(caller).closest('div .options');
	return elem.attr('data-id');
}
var welcome = 'welcome';
var steps = [ 'password-setup', 'content-setup', 'thanks' ];
function changePassword(elem, formElem) {
	$('#password-msg').hide();
	var pwd1 = $(formElem).find('input[name=pwd1]').val();
	var pwd2 = $(formElem).find('input[name=pwd2]').val();
	if (pwd1 != pwd2) {
		$('#password-msg').show()
		$('#password-msg').addClass('warn-msg').html('passwords don\'t match');
	} else if (pwd1 == '') {
	} else {
		$.get('api/welcome.json', {
			'pwd1' : pwd1,
			'pwd2' : pwd2
		}, function(data, success) {
			data = JSON.parse(data);
			console.debug(data, success);
			if (data.result) {
				$(elem).addClass('success');
				goNext(true);
			} else {
				showWarning(data);
			}
		}).fail(function(data, test) {
			console.debug(data, test);
			showWarning(data.statusText);
		})
	}
}
function showWarning(msg) {
	$('#password-msg').show()
	$('#password-msg').addClass('warn-msg').html(msg);
}
function currentStep() {
	return steps[0];
}
var running = false;
function goNext(hideWelcome) {
	if (running) {
		return;
	}
	running = true;
	var step = steps[0];
	steps.splice(0, 1);
	console.debug('step', step);
	console.debug('steps', steps);
	setTimeout(function() {
		if (hideWelcome) {
			$('#' + welcome).fadeOut(1000);
		}
		$('.' + step).fadeOut(1000, function() {
			$('.' + currentStep()).show();
			running = false;
		});
	}, 200);
}

//# sourceMappingURL=app.js.map