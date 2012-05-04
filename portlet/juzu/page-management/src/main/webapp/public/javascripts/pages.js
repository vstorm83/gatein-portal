$(function() {
	
	var finished = false;
	
	$('#PageManagementForm').submit(function() {
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
	
	$("#PageManagementForm").find("select").each(function() {
		$(this).change(function() {
			$("#PageManagementForm").submit();
		});
	})
	
	var autoAjust = function() {
		var pageList =$("#PageListContainer");
		pageList.height("auto");
		var eleHeight = 0; 
		pageList.children().each(function() {
			eleHeight += $(this).height();
		});

		var height = $(document).height() - pageList.height() - 20;
		if(pageList.height() > height && eleHeight > height) {
			pageList.height(height);
			
			var space = 0;
			$(document.body).children().each(function() {
				if(this.tagName == 'SCRIPT') return;
				space += $(this).height();
			});
			
			var freeSpace = $(window).height() - space;
			if(freeSpace > 0) pageList.height(height + freeSpace);
		} else if(eleHeight > 300) {
			pageList.height(300);
		}
	};
	
	autoAjust();
	
	var typingTimer;
	var doneTypingInterval = 1000;
	
	$("#PageManagementForm").find('input[type=text]').each(function() {
		$(this).keyup(function(e) {
			window.clearTimeout(typingTimer);
			if(e.which != 9) {
				typingTimer = setTimeout($.proxy(function() {
					$("#PageManagementForm").submit();
				}, this), doneTypingInterval);
			}
		});
		
		$(this).keydown(function() {
			window.clearTimeout(typingTimer);
		});
	});
});