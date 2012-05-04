$(function() {
	
	//
	var changeOwnerTypeURL = null;
	var changeOwnerIdURL = null;
	$('.jz').find('div').each(function() {
		if($(this).attr('data-method-id') == 'PageSettings.changeOwnerType')
			changeOwnerTypeURL = $(this).attr('data-url');
		else if($(this).attr('data-method-id') == 'PageSettings.changeOwnerID')
			changeOwnerIdURL =$(this).attr('data-url');
	});
	
	//
	var doUpdate = function(data) {
		if(data["PageOwnerID"] != undefined) {
			$("#PageOwnerID").empty();
	    	$(data["PageOwnerID"]).appendTo($("#PageOwnerID"));
		}
    	
    	$("#AccessPermissionList").empty();
    	$(data["AccessPermissionList"]).appendTo($("#AccessPermissionList"));

    	$("#CurrentPermission").empty();
    	$(data["CurrentPermission"]).appendTo($("#CurrentPermission"));
    	
    	if(eval(data["public"])) {
    		$("#publicCheckbox").prop("checked", true);
    		$("#PermissionGrip").hide();
    	} else {
    		$("#publicCheckbox").prop("checked", false);
    		$("#PermissionGrip").show();
    	}
	};

	//
	if($("#publicCheckbox").prop("checked")) $("#PermissionGrip").hide(); 
	else $("#PermissionGrip").show(); 
		
	$("#publicCheckbox").change(function() {
		if($(this).prop("checked")) $("#PermissionGrip").hide();
		else $("#PermissionGrip").show();
	});
	
	//
	$("#PageOwnerType").change(function() {
		var value = $(this).val();
		$.ajax({
			type: "get",
			data: { "ownerType" : value },
		    url: changeOwnerTypeURL,
		    dataType: "json",
		    success: function(data) {
		    	doUpdate(data);

		    	//
		    	$($("#PageOwnerID").find("select")[0]).change(function() {
		    		var value = $(this).val();
		    		$.ajax({
		    			type: "get",
		    			data: { "ownerID" : value },
		    		    url: changeOwnerIdURL,
		    		    dataType: "json",
		    		    success: function(data) {
		    		    	doUpdate(data);
		    		    }
		    		})
		    	});
		    	//
		    }
		});
	});
});