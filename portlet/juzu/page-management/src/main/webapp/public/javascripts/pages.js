$(function() {
	$('#PageMamangementForm').submit(function() {
		$(this).ajaxSubmit({
			target: '#PageList',
			success: function() {
				autoAjust();
			}	
		});
		return false;
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