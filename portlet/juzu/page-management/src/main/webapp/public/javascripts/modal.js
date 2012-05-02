$(function() {
	//
	$("#NewPageModal").modal({
		backdrop: 'static',
		show: false
	});
	
	$('#NewPageModal').appendTo($(document.body));
	$('#NewPageModal').on('hide', function() {
		$('#MembershipSelectorModal').modal('hide');
	});
	
	//
	$("#MembershipSelectorModal").modal({
		backdrop: false,
		show: false
	});
	
	$("#MembershipSelectorModal").appendTo($(document.body));
	
	$("#show").on('click', function() {
		$("#MembershipSelectorModal").modal('show');
	});
	
	//
	var alert = function(type, msg) {
		$("#alert-area").append($("<div class='alert alert-message alert-" + type + " fade in'><a class='close' data-dismiss='alert' href='#'>×</a><p> " + msg + " </p></div>"));
		window.setTimeout(function() { $(".alert-message").alert('close'); }, 2000);
	};
	
	$("#SaveNewPage").on('click', function() {
		var savePageURL = null;
		$('.jz').find('div').each(function() {
			if($(this).attr('data-method-id') == 'PageSettingsController.saveNewPage')
				savePageURL = $(this).attr('data-url');
		});
		var ajaxGet = $.ajax({
			type: "get",
		    url: savePageURL,
		    dataType: "html",
		    success: function(data) {
		    	alert("sucess", data);
		    },
		    error: function(data) {
		    	alert("error", data);
		    }
		});
		$("#NewPageModal").modal('hide');
		$("#MembershipSelectorModal").modal('hide');
	});
});