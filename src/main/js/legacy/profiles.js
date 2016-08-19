var profilesTmpl;
var apiUrlProfileList = '/api/v1/user/profiles/list';

var selectedProfile = {
	'l_profile_id' : 0,
	's_profile_name' : '',
	's_color' : ''
};

var profiles = [ selectedProfile ];

var onProfileSelectedListeners = [];
var onProfilesAvailableListeners = [];
var onProfileDataChangedListeners = [];

function initProfiles() {
	console.debug('init profiles');
	registerOnProfileSelected(function() {
		renderProfiles();
	});

	registerOnProfileDataChange(function() {
		renderProfiles();
	});

	$.getJSON(baseUrl + apiUrlProfileList, '', function(data) {
		profiles = {};

		if (data.entries) {
			profiles = data.entries;
			triggerOnProfilesAvailable();
		} else {
			console.error("error fetching profiles ");
			console.error(data);
		}
	});
}

function renderProfiles() {
	if (!profilesTmpl) {
		profilesTmpl = Handlebars.compile($("#left-menu-profiles-tmpl").html());
	}

	$("#profiles .content").html(profilesTmpl({
		"profiles" : profiles
	}));
}

function registerOnProfileSelected(listener) {
	onProfileSelectedListeners.push(listener);
}

function registerOnProfilesAvailable(listener) {
	onProfilesAvailableListeners.push(listener);
}

function registerOnProfileDataChange(listener) {
	onProfileDataChangedListeners.push(listener);
}

function triggerOnProfilesAvailable() {
	console.debug('trigger profiles avail.', profiles);
	for (var i = 0; i < onProfilesAvailableListeners.length; i++) {
		onProfilesAvailableListeners[i](profiles);
	}
}

function triggerOnProfileSelected(data) {
	for (var i = 0; i < onProfileSelectedListeners.length; i++)
		onProfileSelectedListeners[i](data);
}

function triggerOnProfileDataChanged() {
	for (var i = 0; i < onProfileDataChangedListeners.length; i++)
		onProfileDataChangedListeners[i](profiles);
}

function setSessionProfile(profileId, callback) {
	$.get(baseUrl + "/pages/set_profile.jsp?id=" + profileId, function(data) {
		if (callback) {
			callback(data);
		}
	});
}

function selectProfile(profileId, resetRoute) {
	console.debug('select profile', profileId);
	if (resetRoute) {
		window.location.hash = '';
	}

	if (profileId == 0) {
		return;
	}

	for (var i = 0; i < profiles.length; i++) {
		if (profiles[i].l_profile_id == profileId) {
			selectedProfile = profiles[i];
		}
	}

	if (selectedProfile.l_profile_id == 0) {
		console.error("unknown profile id " + profileId);
	}

	$(".profileColor").css("background-color", "#" + selectedProfile.s_color);
	$(".profile-color").css("color", "#" + selectedProfile.s_color);

	$("#profile").text(selectedProfile.s_profile_name);
	$("#profiles").hide();
	// needed to set the session value in the background.
	setSessionProfile(profileId, function() {
		triggerOnProfileSelected(selectedProfile);
	});
}

function renameProfile(id, name, color) {
	if (id == $("#profile").attr("data-id")) {
		$("#profile").attr("data-id", id);
		$("#profile").text(name);
	}

	for (var i = 0; i < profiles.length; i++) {
		if (profiles[i].l_profile_id == id) {
			profiles[i].s_profile_name = name;
			if (color)
				profiles[i].s_color = color;
			triggerOnProfileDataChanged();
			return;
		}
	}
}

function showCreateNewProfile() {
	showModal("Create new Profile", "#div_new_profile", function() {
		$("#loader").css("background-color", "#" + selectedProfile.s_color);
	});

	$("input[name=profile_name]").focus();

	var pObj = $("input[name=picker]");
	pObj.change(changeProfileColor);
	var picker = new jscolor.color(pObj.get(0), {
		slider : false,
		pickerFaceColor : 'transparent',
		pickerFace : 3,
		pickerBorder : 0,
		pickerInsetColor : 'black'
	});
	picker.fromString('99FF33');
}

function changeProfileColor(color) {
	$("#loader").css("background-color", "#" + color.target.value);
}

function closeNewProfile() {
	hideModal();
}

function createNewProfile(inputName, inputPicker) {
	var name = $("input[name=" + inputName + "]");
	var color = $("input[name=" + inputPicker + "]");

	var queryData = {};
	queryData.n = name.val();
	queryData.c = color.val();

	$.getJSON(baseUrl + "/api/v1/user/profiles/new", queryData, function(data) {
		if (data.success) {
			setSessionProfile(data.profileid, function() {
				location.reload();
			});
			hideModal();
		} else {
			$("#modal_error_text").text("Couldn't add profile. " + data.error);
			$("#modal_error").show();
			return;
		}
	});
}
