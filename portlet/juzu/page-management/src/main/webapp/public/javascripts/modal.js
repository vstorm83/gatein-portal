var juzu = {
	Modal: {},
};

juzu.Modal.alert = function(target, type, msg) {
	$(target).append($("<div class='alert alert-message alert-" + type + " fade in'><a class='close' data-dismiss='alert' href='#'>×</a><p> " + msg + " </p></div>"));
	window.setTimeout(function() { $(".alert-message").alert('close'); }, 2000);
};

$(function() {
	//
	$("#NewPageModal").modal({
		backdrop: 'static',
		show: false
	});
	
	$('#NewPageModal').appendTo($(document.body));
	$('#NewPageModal').on('hide', function() {
		$('#PermissionSelectorModal').modal('hide');
	});
	
	//
	$("#PermissionSelectorModal").modal({
		backdrop: false,
		show: false
	});
	
	$("#PermissionSelectorModal").appendTo($(document.body));
	
	$("#AccessPermissionSelectButton,#EditPermissionSelectButton").on('click', function() {
		juzu.Modal.source = $(this).attr("id");
		$("#PermissionSelectorModal").modal('show');
	});
	
	//
	$("#SaveNewPage").on('click', function() {
		$("#AddNewPageForm").submit();
		var savePageURL = null;
		$('.jz').find('div').each(function() {
			if($(this).attr('data-method-id') == 'PageSettings.saveNewPage')
				savePageURL = $(this).attr('data-url');
		});
		var ajaxGet = $.ajax({
			type: "get",
		    url: savePageURL,
		    dataType: "html",
		    success: function(data) {
		    	juzu.Modal.alert("#alert-area", "sucess", data);
		    },
		    error: function(data) {
		    	juzu.Modal.alert("#alert-area", "error", data);
		    }
		});
		$("#NewPageModal").modal('hide');
		$("#PermissionSelectorModal").modal('hide');
	});
});