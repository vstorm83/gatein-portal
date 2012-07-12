$(function() {
	$(".JuzuNav.treeview").treeview({
		collapsed : true,
		animated : "fast",
		control : "#sidetreecontrol",
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
	}

	var expandAll = function(id) {
		var arr = id.split('-')
		if(arr.length == 1) return false;
		for(var i = 1; i < arr.length; i++) {
			console.log(arr[0] = arr[0] + '-' + arr[i]);
			console.log($(arr[0]));
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
				show('.node-operator.step1');
			});
		});
		var id = $('.JuzuNav.clipboard').text().split('/').join('-');
		expandAll('#' + id);
	}

	var initTreeStep2 = function() {
		$('.treenode-modify .JuzuNav.treeview span').each(function() {
			$(this).on('click', function() {
				$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
					$(this).removeClass('selected');
				});
				$(this).addClass('selected');
				$('.JuzuNav.target').text($(this).attr('node-id'));
				hide('.node-operator.step1');
				show('.node-operator.step2');
			});
		});
	}

	$('.JuzuNav.carousel').carousel({
		pause:null
	});

	$('.node-operator.step1.nextstep2').on('click', function() {
		step2();
	});

	$('.JuzuNav.table .site-selector').on('click', function() {
		step1();
	});

	var startup = function() {
		hide('.node-operator');
		hide('.JuzuNav.step .back');
		carousel('startup');
	};

	var step1 = function() {
		carousel('treenode');
		$('.treenode .JuzuNav.treeview .selected').each(function() {
			$(this).removeClass('selected');
		});
		hide('.node-operator');
		show('.JuzuNav.step .back');
		$('.JuzuNav.step .back').unbind('click');
		$('.JuzuNav.step .back').one('click', function() {
			startup();
		})
	};

	var step2 = function() {
		carousel('treenode-modify');
		$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
			$(this).removeClass('selected');
		});
		$('.treenode-modify .alert').text('Clipboard: ' + $('.JuzuNav.clipboard').text());
		hide('.node-operator');
		show('.JuzuNav.step .back');
		$('.JuzuNav.step .back').unbind('click');
		$('.JuzuNav.step .back').one('click', function() {
			step1();
		})
	};

	startup();
	initTreeStep1();
	initTreeStep2();

	var move = function(mid) {
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
		});
	};

	//Node move up
	$('.node-operator.step1.arrow-up').on('click', function() {
		move("DefaultController.moveUp()");
	});

	//Node move down
	$('.node-operator.step1.arrow-down').on('click', function() {
		move("DefaultController.moveDown()");
	});
})