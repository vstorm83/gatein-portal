$(function() {
	
	//
	var changeOwnerTypeURL = null;
	var changeOwnerIdURL = null;
	$('.jz').find('div').each(function() {
		if($(this).attr('data-method-id') == 'PageSettingsController.changeOwnerType')
			changeOwnerTypeURL = $(this).attr('data-url');
		else if($(this).attr('data-method-id') == 'PageSettingsController.changeOwnerID')
			changeOwnerIdURL =$(this).attr('data-url');
	});
	
	//
	$("#PageOwnerType").change(function() {
		var value = $(this).val();
		$.ajax({
			type: "get",
			data: { "ownerType" : value },
		    url: changeOwnerTypeURL,
		    dataType: "html",
		    success: function(data) {
		    	$("#PageOwnerID").empty();
		    	$(data).appendTo($("#PageOwnerID"));
		    	$($("#PageOwnerID").find("select")[0]).change(function() {
		    		var value = $(this).val();
		    		$.ajax({
		    			type: "get",
		    			data: { "ownerID" : value },
		    		    url: changeOwnerIdURL,
		    		    dataType: "html",
		    		    success: function(data) {
		    		    	
		    		    }
		    		})
		    	});
		    }
		});
	});
});