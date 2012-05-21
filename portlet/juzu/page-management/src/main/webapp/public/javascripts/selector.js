$(function() {
	$("#GroupTree").treeview({
		collapsed : true,
		animated : "fast",
		control: "#GroupTreeControlTrigger",
	});
	
	var breadcrumbURL = $('.jz').jzURL('PagePermissionSelector.renderBreadcrumb');
	
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
			if($("#GroupIdSelected").val() == '') 
			{
				juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a group");
			}
			else
			{
				$("#MembershipSelected").val($(this).text());
				$(this).addClass("active");
			}
		});
	});
	
	var updateEditPermission = function(groupId, membership) {
		var html = "<p>Group ID: <span  style='color: #0088CC;'>" + groupId + "</span></p>";
	     html += "<p>Membership: <span  style='color: #0088CC;'>" + membership + "</span></p>";
	     $("#CurrentEditPermission").empty();
	     $(html).appendTo($("#CurrentEditPermission"));
	     
	     $("#AddNewPageForm input[name='editPermission']").val(membership + ":" + groupId);
	     $("#PermissionSelectorModal").modal('hide');
	};
	
	var addAccessPermission = function(groupId, membership) {
		var html = "<tr><td>" + groupId + "</td><td>" + membership + "</td><td><a>Delete</a></td></tr>"
		$(html).appendTo($("#AccessPermissionList"));
		
		var accessPermission = $("#AddNewPageForm input[name='accessPermission']").val();
		if(accessPermission != '') accessPermission += ",";
		accessPermission +=  membership + ":" + groupId;
		$("#AddNewPageForm input[name='accessPermission']").val(accessPermission);
		$("#PermissionSelectorModal").modal('hide');
	};
	
	$("#DoneSelectPermissionButton").on("click", function() {
		if($("#MembershipSelected").val() == '') 
		{
			juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a membership");	
		}
		else if($("#GroupIdSelected").val() == '')
		{
			juzu.Modal.alert("#PermissionSelectorAlertArea", "error", "Select a group");
		}
		else 
		{
			$("#MembershipList").find("li.active").each(function() {
				$(this).removeClass("active");
			});
		}
		
		if(juzu.Modal.source == "EditPermissionSelectButton")
		{
			updateEditPermission($("#GroupIdSelected").val(), $("#MembershipSelected").val());
		}
		else if(juzu.Modal.source == "AccessPermissionSelectButton") 
		{
			addAccessPermission($("#GroupIdSelected").val(), $("#MembershipSelected").val());
		}
	});
})