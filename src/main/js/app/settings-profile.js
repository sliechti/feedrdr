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
		if (data.success) {
			setSessionProfile(data.profileid, function() {
				selectProfile(data.profileid);
			});
			hideModal();
		} else {
			settingsError(data.error);
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
	var i = $('<i class="fa fa-plus"></i>').on('click', forwardSaveProfile);
	var actions = $(tmpl).find('.actions');
	actions.find('.fa-remove').hide()
	actions.append(i);

	$('#profile-list').append($(tmpl));
	attachColorPicker($('#picker_profile_0'));
	disableAddBox();
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
