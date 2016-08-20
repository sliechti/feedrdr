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
	console.debug('add new profile');
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
	disableAddBox();
	$('#name_profile_0').focus();
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
