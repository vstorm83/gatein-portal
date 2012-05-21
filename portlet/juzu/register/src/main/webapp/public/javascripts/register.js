$(function() {
	var typingTimer;
	var doneTypingInterval = 1500;
	
	$('#form').submit(function() {
		$(this).ajaxSubmit({
			target: '#result'
		});
		return false;
	});
	
	var showStatus = function(target, msg, option) {
		var container = $(target).parents('.control-group');
		switch(option) {
			case 'error':
				container.removeClass('success').addClass('error');
				$(target).nextAll('.checked').val(false);
				break;
			case 'success':
				container.removeClass('error').addClass('success');
				$(target).nextAll('.checked').val(true);
				break;
		};
		$(target).nextAll('.status').show().text(msg);
    	$(target).nextAll('.help').hide();
	};
	
	var showHelp = function(target) {
		var container = $(target).parents('.control-group');

		if(container.hasClass('success')) {
			$(target).nextAll('.help').hide();
			$(target).nextAll('.status').show();
		} else if(container.hasClass('error')){
			container.removeClass('error');
			$(target).nextAll('.help').show();
			$(target).nextAll('.status').hide();
		}
	}
	
	var doCheck = function(elt) {
		var actionURL = $(elt).jzURL("RegisterController.validate");
		var param = {};
		
		param["name"] = $(elt).attr("name");
		param["value"] = $(elt).val();
		var ajaxGet = $.ajax({
			type: "get",
			data: param,
			async: false,
		    url: actionURL,
		    dataType: "json",
		    success: function(data) {
		    	result = eval(data["return"]);
		    	msg = data["msg"];
		    	if(result) showStatus(elt, msg, 'success');
		    	else showStatus(elt, msg, 'error');
		    }
		});
	};
	
	$('#form').find('input').each(function() {
		$(this).on('focus', function() {
			showHelp(this);
		});
		
		$(this).on('blur', function() {
			if($(this).hasClass('required') && $(this).val() == '') {
		    	showStatus(this, 'This field is required', 'error');
		    	return;
			}
			if($(this).next().hasClass('equalTo')) {
				var equalItem = $($(this).next().val()); 
				if(equalItem.val() != $(this).val()) {
					$(this).val('');
					showStatus(this, 'Need equals with password', 'error');
					return;
				} else {
					showStatus(this, 'Ok', 'success');
				}
			}
			if($(this).hasClass('doCheck')) doCheck(this);
		});
		
		if($(this).hasClass('doCheck')) {
			$(this).keyup(function(e) {
				window.clearTimeout(typingTimer);
				if(e.which != 9) {
					typingTimer = setTimeout($.proxy(function() {
						doCheck(this);
					}, this), doneTypingInterval);
				}
			});
			
			$(this).keydown(function() {
				window.clearTimeout(typingTimer);
			});
		}
	});
	
	var required = function(elt) {
		var rules = {
			username: true,
			password: true,
			confirmPassword: {
				required: true,
				equalTo: "#password"
			},
			firstName: true,
			lastName: true,
			emailAddress: true,
			captcha: true
		};
	
		$.each(rules, function(k, v) {
			var target = $('#' + k);
			
			if(v instanceof Object) {
				// confirm password
				if(v.required && target.val() == '') {
					showStatus(this, 'This field is required', 'error');
					target.focus();
					return false;
				}
				if(target.val() != $(v.equalTo).val()) {
					showStatus(this, 'Confirm password incorrect', 'error');
					target.focus();
					target.val('');
					return false;
				}
			} else {
				// check each item
				if(v && target.val() == '') {
					showStatus(this, 'This field is required', 'error');
					target.focus();
					return false;
				}
			}
		});
		return true;
	};
	
	var correct = true;	
	$('#subscribe').click(function() {
		$('#form').find('.error').each(function() {
			correct = false;
			$(this).find('input[type=text]').focus();
		});

		correct = correct && required(this);
		
		if(correct) $('#form').submit();
	});
	
	$('#reset').click(function() {
		$('#form').clearForm();
		$('#form').find('.control-group').each(function() {
			$(this).removeClass('success').removeClass('error');
			showHelp($(this).find('.help-inline').each(function() {
				$(this).hide();
			}));
		});
	});
});