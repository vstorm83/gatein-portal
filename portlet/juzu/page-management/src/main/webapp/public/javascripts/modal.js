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
	
	$('#NewPageModal').on('hide', function() {
		$('#PermissionSelectorModal').modal('hide');
	});
	
	//
	$("#PermissionSelectorModal").modal({
		backdrop: false,
		show: false
	});
	
	$("#AddNewPageForm").appendTo($(document.body));
	
	$("#AccessPermissionSelectButton,#EditPermissionSelectButton").on('click', function() {
		juzu.Modal.source = $(this).attr("id");
		$("#PermissionSelectorModal").modal('show');
	});
	
	$('#AddNewPageForm').submit(function() {
		$(this).ajaxSubmit({
			target: '#alert-area',
			success: function() {
		    	juzu.Modal.alert("#alert-area", "sucess", "Create a new page successfully");
		    	$('#AddNewPageForm').resetForm();
		    },
		    error: function() {
		    	juzu.Modal.alert("#alert-area", "error", "A error when create a new page ");
		    }
		});
		return false;
	});
	
	//
	$("#SaveNewPage").on('click', function() {
		$("#NewPageModal").modal('hide');
		$("#PermissionSelectorModal").modal('hide');
		$("#AddNewPageForm").submit();
	});
});