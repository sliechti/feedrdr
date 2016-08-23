$(document).on('keyup', function(e) {
	if (e.keyCode == 27) {
		openLeftBar();
	}
});

$(document).ready(function() {
	var hm = new Hammer(document);
	hm.get('swipe').set({
		direction : Hammer.DIRECTION_ALL,
		pointer : 1,
		treshold : 10,
		velocity : 0.3
	});
	hm.on('swipeleft', function(ev) {
		closeLeftBar();
	});
	hm.on('swiperight', function(ev) {
		openLeftBar();
	});
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
	}, 200, 'swing', function() {
		lm.hide();
	});
}

function openAnimate() {
	var lm = $(leftMenuId);
	lm.css('left', lm.width() * -1);
	lm.show();
	lm.animate({
		left : 0
	}, 200);
}

function closeLeftBar() {
	closeProfiles();
	closeAnimate();
	$(document).off('keyup.leftbar');
	$(document).off('mousedown.leftbar');
	$(leftMenuId).off('swipeleft');
}

function openLeftBar() {
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
		$(document).on(
				'mousedown.leftbar',
				function(e) {
					var container = $(leftMenuId);
					if (!container.is(e.target)
							&& container.has(e.target).length === 0) {
						closeLeftBar();
					}
				});
	}
}

function lmToggleProfiles() {
	var p = $(profilesId);
	$(profilesId).toggle();
	// checkProfileAngle();
}

function closeProfiles() {
	$(profilesId).hide();
	// checkProfileAngle();
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
	showMsg(elemId, msg, 'msg-error');
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
		console.debug('forward save profile ', data);
		if (data.success) {
			settingsInfo('Profile created ' + queryData.n);
			setSessionProfile(data.profileid, function() {
				selectProfile(data.profileid);
				location.reload();
			});
		} else {
			settingsError('Erorr creating new profile: ' + data.error);
		}
	});
}
function addNewProfile() {
	var tmpl = $(Handlebars.partials['tmplprofile'](
			{
				'l_profile_id': '0',
				's_profile_name': '',
				's_color': '#173C4F'
			}
		));
	var i = $('<input type="button" value="Add" onclick="forwardSaveProfile()" />');
	var actions = $(tmpl).find('.actions');
	actions.html(i);

	$('#profile-list').append($(tmpl));
	attachColorPicker($('#picker_profile_0'));
	disableAddNew();
	$('#name_profile_0').focus();
}

function disableAddNew() {
	$('#addNewProfile').addClass('disabled').off('click');
}

function spActivateBurron(id) {
	$(id).attr('disabled', false);
}

function saveProfile(id, caller) {
	var name = $("#name_profile_" + id).val();
	var color = $("#picker_profile_" + id).val();
	$(caller).attr('disabled', true);

	var queryData = {};
	queryData.pid = id;
	queryData.n = name;
	queryData.c = color;

	$.getJSON(baseUrl + '/api/v1/user/profiles/save', queryData, function(data) {
		if (data.count > 0) {
			settingsInfo('Profile saved ' + name);
		} else {
			$(caller).attr('disabled', false);
			settingsError('Couldn\'t save profile');
		}
	});
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
				$('#addNewProfile').on('click', addNewProfile);
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
var steps = [ 'password-setup', 'thanks' ];
function changePassword(elem, formElem) {
	$('#password-msg').hide();
	var pwd1 = $(formElem).find('input[name=pwd1]').val();
	var pwd2 = $(formElem).find('input[name=pwd2]').val();
	if (pwd1 != pwd2) {
		showWarning('Passwords don\'t match');
	} else if (pwd1 == '') {
		showWarning('Empty password');
	} else {
		$.post(baseUrl + '/wizard', {
			'pwd1' : pwd1,
			'pwd2' : pwd2,
			'wizard-step' : 1
		}, function(data) {
			if (data.success) {
				$(elem).addClass('success');
				goNext(true);
			} else if (data.code == 100) {
				showWarning(data.error);
				$("#btn-pwd-change").addClass('success').html('Skip step');
				$("#btn-pwd-change").removeAttr("onclick");
				$("#btn-pwd-change").on("click", goNext);
			} else if (data.error) {
				showWarning(data.error);
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