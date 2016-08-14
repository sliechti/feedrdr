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