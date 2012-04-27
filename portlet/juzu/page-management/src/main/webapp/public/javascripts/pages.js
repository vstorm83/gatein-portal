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
	
	//
	$("#NewPageModal").modal({
		backdrop: 'static',
		show: false
	});
	
	$('#NewPageModal').appendTo($(document.body));
	$('#NewPageModal').on('hide', function() {
		$('#MembershipSelectorModal').modal('hide');
	});
	
	//
	$("#MembershipSelectorModal").modal({
		backdrop: false,
		show: false
	});
	
	$("#MembershipSelectorModal").appendTo($(document.body));
	
	$("#show").on('click', function() {
		$("#MembershipSelectorModal").modal('show');
	});
	
	//
	var alert = function(type, msg) {
		$("#alert-area").append($("<div class='alert alert-message alert-" + type + " fade in'><a class='close' data-dismiss='alert' href='#'>×</a><p> " + msg + " </p></div>"));
		window.setTimeout(function() { $(".alert-message").alert('close'); }, 2000);
	};
	
	$("#SaveNewPage").on('click', function() {
		var savePageURL = null;
		$('.jz').find('div').each(function() {
			if($(this).attr('data-method-id') == 'NewPageController.saveNewPage')
				savePageURL = $(this).attr('data-url');
		});
		var ajaxGet = $.ajax({
			type: "get",
		    url: savePageURL,
		    dataType: "html",
		    success: function(data) {
		    	alert("sucess", data);
		    },
		    error: function(data) {
		    	alert("error", data);
		    }
		});
		$("#NewPageModal").modal('hide');
		$("#MembershipSelectorModal").modal('hide');
	});
});