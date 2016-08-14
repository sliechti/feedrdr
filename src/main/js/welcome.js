var welcome = 'welcome';
var steps = [ 'password-setup', 'content-setup', 'thanks' ];
function changePassword(elem, formElem) {
	$('#password-msg').hide();
	var pwd1 = $(formElem).find('input[name=pwd1]').val();
	var pwd2 = $(formElem).find('input[name=pwd2]').val();
	if (pwd1 != pwd2) {
		$('#password-msg').show()
		$('#password-msg').addClass('warn-msg').html('passwords don\'t match');
	} else if (pwd1 == '') {
	} else {
		$.get('api/welcome.json', {
			'pwd1' : pwd1,
			'pwd2' : pwd2
		}, function(data, success) {
			data = JSON.parse(data);
			console.debug(data, success);
			if (data.result) {
				$(elem).addClass('success');
				goNext(true);
			} else {
				showWarning(data);
			}
		}).fail(function(data, test) {
			console.debug(data, test);
			showWarning(data.statusText);
		})
	}
}
function showWarning(msg) {
	$('#password-msg').show()
	$('#password-msg').addClass('warn-msg').html(msg);
}
function currentStep() {
	return steps[0];
}
var running = false;
function goNext(hideWelcome) {
	if (running) {
		return;
	}
	running = true;
	var step = steps[0];
	steps.splice(0, 1);
	console.debug('step', step);
	console.debug('steps', steps);
	setTimeout(function() {
		if (hideWelcome) {
			$('#' + welcome).fadeOut(1000);
		}
		$('.' + step).fadeOut(1000, function() {
			$('.' + currentStep()).show();
			running = false;
		});
	}, 200);
}
