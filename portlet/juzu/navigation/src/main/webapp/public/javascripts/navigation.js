$(function() {
	$('.JuzuNav.left .add').on('click', function() {
		load("DefaultController.viewAddSite()");
	});
	
	$(".JuzuNav.treeview").treeview({
		collapsed : true,
		animated : "fast",
		prerendered : true,
		persist : "location"
	});

	var state = {
			"current" : null,	
			"startup" : 0,
			"treenode" :  1,
			"treenode-modify" : 2
	};

	var show = function(clazz) {
		$(clazz).each(function() {
			$(this).show();
		})
	};

	var hide = function(clazz) {
		$(clazz).each(function() {
			$(this).hide();
		})
	}; 

	var carousel = function(step) {
		$('.JuzuNav.carousel').carousel(state[step]);
		$('.JuzuNav.carousel').carousel('pause');
		state['current'] = step;
	};
	
	var expandAll = function(id) {
		var arr = id.split('-')
		if(arr.length == 1) return false;
		for(var i = 1; i < arr.length; i++) {
			arr[0] = arr[0] + '-' + arr[i];
			$(arr[0]).click();
		}
	};

	var initTreeStep1 = function() {
		$('.treenode .JuzuNav.treeview span').each(function() {
			$(this).on('click', function() {
				$('.treenode .JuzuNav.treeview .selected').each(function() {
					$(this).removeClass('selected');
				});
				$(this).addClass('selected');
				$('.JuzuNav.clipboard').text($(this).attr('node-id'));
				show('.JuzuNav.right');
			});
		});
	};

	var initTreeStep2 = function() {
		$('.treenode-modify .JuzuNav.treeview span').each(function() {
			$(this).on('click', function() {
				$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
					$(this).removeClass('selected');
				});
				$(this).addClass('selected');
				$('.JuzuNav.target').text($(this).attr('node-id'));
				show('.JuzuNav.left .node-operator');
			});
		});
	};
	
	var startup = function() {
		hide('.JuzuNav.left .back');
		hide('.JuzuNav.left .edit');
		hide('.JuzuNav.left .node-operator');
		
		//
		
		carousel('startup');
	};

	var step1 = function() {
		carousel('treenode');
		hide('.JuzuNav.left .node-operator');
		show('.JuzuNav.left .back');
		$('.JuzuNav.left .back').unbind('click');
		$('.JuzuNav.left .back').one('click', function() {
			startup();
		})
	};

	var step2 = function() {
		carousel('treenode-modify');
		$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
			$(this).removeClass('selected');
		});
		$('.treenode-modify .alert').text('Clipboard: ' + $('.JuzuNav.clipboard').text());
		hide('.JuzuNav.step2');
		show('.JuzuNav.left .back');
		$('.JuzuNav.left .back').unbind('click');
		$('.JuzuNav.left .back').one('click', function() {
			step1();
		})
	};

	$('.JuzuNav.carousel').carousel({
		pause:null
	});

	$('.JuzuNav.right .node-operator.nextstep2').on('click', function() {
		step2();
		var html = $('.treenode .JuzuNav.treeview').html();
		var tree = $(".treenode-modify .JuzuNav.treeview");
		$(tree).html(null);
		var update = $(html).appendTo(tree);
		$(tree).treeview({
			add: update
		})
		$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
			$(this).removeClass('selected');
		});
		initTreeStep2();
	});

	$('.JuzuNav.table .site-selector').on('click', function() {
		$('.JuzuNav.clipboard').text($(this).attr('site-id'));
		load("DefaultController.loadNavigation()");
		step1();
		show('.JuzuNav.left .edit');
	});

	startup();
	initTreeStep1();

	var load = function(mid) {
		var id = $('.JuzuNav.clipboard').text();
		$(".treenode .JuzuNav.treeview").jzLoad(mid, {
			nodeId : id
		}, function(data) {
			$(this).html(null);
			var update = $(data).appendTo(this);
			$(this).treeview({
				add : update
			});

			initTreeStep1();
			var id = $('.JuzuNav.clipboard').text().split('/').join('-');
			expandAll('.treenode .JuzuNav.treeview span#' + id);
		});
	};
	
	var modify = function(mid, src, dest) {
		$('.treenode .JuzuNav.treeview').jzLoad(mid, {
			srcId : src,
			destId: dest
		}, function(data) {
			$(this).html(null);
			var update = $(data).appendTo(this);
			$(this).treeview({
				add : update
			});

			initTreeStep1();
			var id = $('.JuzuNav.target').text().split('/').join('-');
			expandAll('.treenode .JuzuNav.treeview span#' + id);
		})
	}

	//Node move up
	$('.JuzuNav.right .arrow-up').on('click', function() {
		load("DefaultController.moveUp()");
	});

	//Node move down
	$('.JuzuNav.right .arrow-down').on('click', function() {
		load("DefaultController.moveDown()");
	});
	
	//Node delete
	$('.JuzuNav.right .delete').on('click', function() {
		load("DefaultController.delete()");
	});
	
	//
	$('.JuzuNav.right .copy, .JuzuNav.right .clone, .JuzuNav.right .cut').each(function() {
		$(this).on('click', function() {
			if($(this).hasClass('copy')) {
				$('.JuzuNav.method').text('copy');
			} else if($(this).hasClass('clone')) {
				$('.JuzuNav.method').text('clone');
			} else if($(this).hasClass('cut')) {
				$('.JuzuNav.method').text('cut');
			}
		});
	})
	
	$('.JuzuNav.left .paste').on('click', function() {
		var src = $('.JuzuNav.clipboard').text();
		var desc = $('.JuzuNav.target').text();
		var method = $('.JuzuNav.method').text();
		switch(method) {
			case "copy":
				modify("DefaultController.copy()", src, desc);
				break;
			case "clone":
				modify("DefaultController.clone()", src, desc);
				break;
			case "cut":
				modify("DefaultController.cut()", src, desc);
				break;
		}
		step1();
	});
})