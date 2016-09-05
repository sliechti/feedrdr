var searchId = '#search';
var tmplSourceResult = {};
var lastLocation = '';
var searched = false;

function hideSearch() {
	var hi = $('#header-input');
	var hc = $('#header-content');
	hc.show();
	hi.addClass('hide');
	if (searched) {
		if (location.href != lastLocation) {
			window.location.href = lastLocation;
		} else {
			location.reload();
		}
	}
}

function showSearch(caller) {
	lastLocation = location.href;
	var hi = $('#header-input');
	var hc = $('#header-content');
	hc.hide();
	hi.removeClass('hide');
	console.debug('header-input', hi);
	hi.show();
	var inp = hi.find('input');
	inp.focus();
}

function search(ev) {
	if (ev) {
		if (ev.keyCode == 13) {
			var query = {};
			query.title = $(searchId).val();
			apiFindSource(query, function(data) {
				searched = true;
				$('#stream-header .content').html('');
				$('#stream-entries').html(tmplSourceResult({
					entries : data.entries
				}));
			});
		} else if (ev.keyCode == 27) {
			hideSearch();
		}
	}
}

$(document).ready(function() {
	var html = $('#search-sources-result').html();
	if (html) {
		$(searchId).on('keyup', search);
		tmplSourceResult = Handlebars.compile(html);
	}
});
