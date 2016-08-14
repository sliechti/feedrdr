var spTmpl = spTmpl || {};

function settingsError(msg) {
	showErrorMsg('#settings-msg', msg);
}

function loadColorPickers() {
	$('.profile-row').find('input[data-color]').each(function(idx, o) {
		var pObj = $(o);
		var picker = new jscolor.color(pObj.get(0), {
			slider : true,
			pickerFaceColor : 'transparent',
			pickerMode : 'HSV',
			pickerFace : 1,
			pickerBorder : 0,
			pickerInsetColor : 'black'
		});
		picker.fromString(pObj.attr('data-color'));
	});
}

function saveProfileRow(e) {
	alert('save profile row');
	console.debug(e.data.root);
	e.data.root.find('.fa-plus').remove();
	e.data.root.find('.fa-remove').show();
}

function addNewProfile() {
	console.debug('add new profile');
	var tmpl = $(Handlebars.partials['tmplprofile']({'profiles' :
			[{
				'l_profile_id': -1,
				's_profile_name': '',
				's_color': '#173C4F'
			}]
		}));
	var i = $('<i class="fa fa-plus"></i>').on('click', {root: tmpl}, saveProfileRow);
	var actions = $(tmpl).find('.actions');
	actions.find('.fa-remove').hide()
	actions.append(i);

	$('#profile-list').append($(tmpl));
	disableAddBox();
}

function disableAddBox() {
	$('#settings-profile').find('.box').addClass('disabled').off('click');
}

function deleteProfile() {
	alert('delete profile');
}

$(document).ready(function() {
	var tmplprofile = $('#tmpl-settings-profile').html();
	if (tmplprofile) {
		Handlebars.registerPartial('tmplprofile', tmplprofile);
		spTmpl = Handlebars.compile($('#tmpl-settings-profile-list').html());
		$.get('api/profiles.json', function(data, success) {
			if (data.entries) {
				$('#profile-list').html(spTmpl({
					'profiles' : data.entries
				}));
				var sp = $('#settings-profile');
				sp.find('.box').on('click', addNewProfile);
			}
			loadColorPickers();
		}).fail(function (xhr) {
			settingsError(xhr.statusText);
		});
	}
});
