var baseUrl = "/";
var closeModalCallback = {};
var MODALBOX_SELECTOR = "#modalBox";

function setBaseUrl(url) {
	baseUrl = url;
}

Handlebars.registerHelper('formatUnixTs', function(str) {
	return new Date(str).toLocaleString();
});

Handlebars.registerHelper('toLowerCase', function(str) {
	return str.toLowerCase();
});

Handlebars.registerHelper('toUpperCase', function(str) {
	return str.toUpperCase();
});

function cut(str, len) {
	if (str && str.length > len) {
		return str.substring(0, len) + "...";
	}
	return str;
}

Handlebars.registerHelper('cut', function(str, len) {
	return cut(str, len);
});

function toggleCheckbox(name) {
	$(name).prop('checked', !$(name).prop('checked'));
}


function hideModal() {
	$(MODALBOX_SELECTOR).keydown(null);
	$(MODALBOX_SELECTOR).hide();
	if (closeModalCallback) {
		closeModalCallback();
	}
}

function modalError(error) {
	$("#modalTitle").css("background-color", "red");
	$("#modalTitle").text(error);
}

function showModal(title, contentSelector, closeCallback, postInitCallback) {
	$("#modalTitle").css("background-color", "");
	$(MODALBOX_SELECTOR).show();
	$("#modalTitle").text(title);
	var content = $(contentSelector);
	content.remove(); // so we don't have duplicates.
	$("#modalContent").html(content.html());

	this.closeModalCallback = closeCallback;
	
	$(MODALBOX_SELECTOR).keydown(function(e) {
	  if (e.keyCode == 27) {
		  hideModal();
	  }
	});	
	
	if (postInitCallback) {
		postInitCallback($(MODALBOX_SELECTOR));
	}
}

function toogleSmallMenu() {
	$("#nav").toggle();
	$("#content").css("margin-top", "50px");
}
