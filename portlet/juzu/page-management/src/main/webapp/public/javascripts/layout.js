$(function() {

	var changeLayoutCateogryURL = $(".jz").jzURL("PageLayout.changeLayoutCategory");
	
	$("#LayoutCategories").change(function() {
		var value = $(this).val();
		$.ajax({
			type: "get",
			data: { "category" : value },
		    url: changeLayoutCateogryURL,
		    dataType: "json",
		    success: function(data) {
		    	$("#LayoutItems").empty();
		    	$(data["LayoutItems"]).appendTo($("#LayoutItems"));
		    	$("#LayoutPreview").empty();
		    	$(data["LayoutPreview"]).appendTo($("#LayoutPreview"));
		    }
		});
	});
	
	$("#LayoutItems").on("click", "li", function() {
		$("#LayoutItems").find("li.active").each(function() {
			$(this).removeClass("active");
		});
		$(this).addClass("active");
		$(this).find("a").each(function() {
			var name = $(this).text();
			var value = $(this).attr("value");
			var icon = $(this).attr("icon");
			
			var html = "<div style='text-align: center; margin-top: 20px;'>" + name + "</div>";
		    html += "<div class='" + icon + "' style='margin: auto;'></div>";
		    html += "<input type='hidden' name='layout' value='" + value + "' />";
		     
		    $("#LayoutPreview").empty();
		    $(html).appendTo($("#LayoutPreview"));
		});
	});
});