$(function() {
	
	var finished = false;
	
	$('#PageMamangementForm').submit(function() {
		$(this).ajaxSubmit({
			target: '#PageList',
			success: function() {
				finished = false;
				autoAjust();
			}	
		});
		return false;
	});
	
	$("#PageListContainer").scroll(function() {
		
		if(finished) return;
		
		var totalElementHeight = 0;
		$(this).children().each(function() {
			totalElementHeight += $(this).height();
		});
		
		if(($(this).scrollTop() + $(this).height()) > totalElementHeight) {
			var nextPageUrl = this.PagesApplication().nextPage();
			var ajaxGet = $.ajax({
				type: "get",
			    url: nextPageUrl,
			    dataType: "html",
			    success: function(data) {
			    	if(data == '') finished = true;
			    	$("#PageList").append(data);
			    }
			});
		}
	});
	
	var autoAjust = function() {
		var pageList =$("#PageListContainer");
		pageList.height("auto");
		var eleHeight = 0; 
		pageList.children().each(function() {
			eleHeight += $(this).height();
		});
		
		var height = $(document).height() - pageList.height() - 20;
		if(pageList.height() > height && eleHeight > height) pageList.height(height);
	};
	
	autoAjust();
});