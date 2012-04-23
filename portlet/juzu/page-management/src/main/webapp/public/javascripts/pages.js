$(function() {
	$('#PageMamangement').submit(function() {
		$(this).ajaxSubmit({
			target: '#pageList'
		});
		return false;
	});
});
