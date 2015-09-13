var r = {};

var profilesSettingsTmpl = {};

function initSettings() {
	profilesSettingsTmpl = Handlebars.compile($("#all_profiles_settings_tmpl").html());

	var router = new Router().init();

	router.on('/v/:panel', loadSettingsView);
	router.on('/v/:panel/:val', loadSettingsView);

	r = router.getRoute();
}

function setupProfileView(data) {
	$("#profile_settings_list").html(profilesSettingsTmpl({
		"profiles" : data
	}));
	loadColorPickers();
}

function loadSettingsView(str, val) {
	console.log("show panel " + str + ", val " + val);
	$("#content_panel").children().each(function(i, o) {
		if (o.id == "error" || o.id == "info")
			return;
		$(o).hide();
	});
	$("#" + str).show();
}

function showMessage(selector, msg) {
	if (!msg || msg == '') {
		$("#" + selector).hide();
		return;
	}

	$("#" + selector + "_text").text(msg);
	$("#" + selector).show();
}

function showError(msg) {
	showMessage('error', msg);
}

function showInfo(msg) {
	showMessage('info', msg);
}

function loadColorPickers() {
	$("#profile_settings_list").children("div").each(function(i, o) {
		var pObj = $("#picker_" + o.id);
		pObj.change(changeProfileColor);
		var picker = new jscolor.color(pObj.get(0), {
			slider : true,
			pickerFaceColor : 'transparent',
			pickerMode : "HSV",
			pickerFace : 1,
			pickerBorder : 0,
			pickerInsetColor : 'black'
		});
		picker.fromString(pObj.attr("data-color"));
	});
}

function saveProfile(id) {
	var name = $("#name_profile_" + id).val();
	var color = $("#picker_profile_" + id).val();

	var queryData = {};
	queryData.pid = id;
	queryData.n = name;
	queryData.c = color;

	$.getJSON(baseUrl + '/api/v1/user/profiles/save', queryData, function(data) {
		if (data.count > 0) {
			$("#profile_" + id).addClass("saved");
			setTimeout(function() {
				$("#profile_" + id).removeClass("saved");
			}, 1500);

			renameProfile(id, name, color);
		} else {
			console.error("error saving profile");
			console.error(data);
		}
	});
}

function deleteProfile(id) {
	if (confirm("Are you sure? Deleting a profile means:\n"
			+ "* All recently read and saved information for this profile is deleted. \n"
			+ "* Any stream groups not used by other profiles are deleted.")) {
		showError('');// clears

		var queryData = {};
		queryData.pid = id;

		console.log("deleting profile  " + id);

		$.getJSON(baseUrl + '/api/v1/user/profiles/delete', queryData, function(data) {
			if (data.count > 0) {
				location.reload();
			} else if (data.error) {
				console.log(data.error);
				showError(data.error);
			} else {
				console.log(data);
			}

		})
	}
}