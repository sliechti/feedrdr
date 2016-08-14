
$(document).on('keyup', function(e) {
	if (e.keyCode == 27) {
		openLeftBar();
	}
});
$(document).on('swiperight', function() {
	openLeftBar();
});


