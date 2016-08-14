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
