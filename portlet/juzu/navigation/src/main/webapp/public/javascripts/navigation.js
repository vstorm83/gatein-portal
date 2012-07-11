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
	
	$('.JuzuNav.carousel').carousel({
		pause:null
	});
	
	$('.treenode .JuzuNav.treeview span').each(function() {
		$(this).on('click', function() {
			$('.treenode .JuzuNav.treeview .selected').each(function() {
				$(this).removeClass('selected');
			});
			$(this).addClass('selected');
			show('.node-operator.step2');
		});
	});
	
	$('.node-operator.step2.motion').on('click', function() {
		step2();
	});
	
	$('.treenode-modify .JuzuNav.treeview span').each(function() {
		$(this).on('click', function() {
			$('.treenode-modify .JuzuNav.treeview .selected').each(function() {
				$(this).removeClass('selected');
			});
			$(this).addClass('selected');
			show('.node-operator.step3');
			hide('.node-operator.step2')
		});
	});
	
	$('.JuzuNav.table .site-selector').each(function() {
		$(this).on('click', function() {
			step1();
		})
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
		show('.node-operator.step1');
		show('.JuzuNav.step .back');
		$('.JuzuNav.step .back').one('click', function() {
			startup();
		})
	};
	
	var step2 = function() {
		carousel('treenode-modify');
		hide('.node-operator');
		show('.node-operator.step3');
		show('.JuzuNav.step .back');
		$('.JuzuNav.step .back').one('click', function() {
			step1();
		})
	};
	
	startup();
})