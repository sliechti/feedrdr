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
