var welcome = 'welcome';
var steps = [ 'password-setup', 'thanks' ];
function changePassword(elem, formElem) {
	$('#password-msg').hide();
	var pwd1 = $(formElem).find('input[name=pwd1]').val();
	var pwd2 = $(formElem).find('input[name=pwd2]').val();
	if (pwd1 != pwd2) {
		showWarning('Passwords don\'t match');
	} else if (pwd1 == '') {
		showWarning('Empty password');
	} else {
		$.post('wizard', {
			'pwd1' : pwd1,
			'pwd2' : pwd2,
			'wizard-step' : 1
		}, function(data) {
			if (data.success) {
				$(elem).addClass('success');
				goNext(true);
			} else if (data.code == 100) {
				showWarning(data.error);
				$("#btn-pwd-change").addClass('success').html('Skip step');
				$("#btn-pwd-change").removeAttr("onclick");
				$("#btn-pwd-change").on("click", goNext);
			} else if (data.error) {
				showWarning(data.error);
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
