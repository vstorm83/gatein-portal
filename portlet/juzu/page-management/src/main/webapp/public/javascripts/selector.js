$(function() {
	$("#GroupTree").treeview({
		collapsed : true,
		animated : "fast",
		control: "#GroupTreeControlTrigger",
	});
	
	var breadcrumbURL = null;
	$('.jz').find('div').each(function() {
		if($(this).attr('data-method-id') == 'PagePermissionSelector.renderBreadcrumb')
			breadcrumbURL = $(this).attr('data-url');
	});
	
	$("#GroupTree").find("span").each(function() {
		$(this).on('click', function() {
			var value = $(this).attr("group-id")
			$.ajax({
				type: "get",
				data: { "groupId" : value },
			    url: breadcrumbURL,
			    dataType: "html",
			    success: function(data) {
			    	$("#GroupSelectedBreadcrumb").empty();
			    	$(data).appendTo($("#GroupSelectedBreadcrumb"));
			    	$("#GroupIdSelected").val(value);
			    }
			});
		});
	});
	
	$("#MembershipList").find("li").each(function() {
		$(this).on('click', function() {
			$("#MembershipList").find("li.active").each(function() {
				$(this).removeClass("active");
			});
			$(this).addClass("active");
			if($("#GroupIdSelected").val() == '') {
				juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a group");
			}
			else
				$("#MembershipSelected").val($(this).text());
		});
	});
	
	$("#DoneSelectPermissionButton").on("click", function() {
		alert(juzu.Modal.source);
		if($("#MembershipSelected").val() == '') 
			juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a membership");
		else if($("#GroupIdSelected").val() == '') 
			juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a group");
		else {
			
		}
	});
	
	var updateEditPermission = function() {
		
	};
	
	var updateAccessPermission = function() {
		
	};
})