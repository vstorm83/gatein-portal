var juzu = {
	Sitemap: {},
};

juzu.Sitemap.lazyLoad = function() {
	var child = $(this).find("ul");
	if(child.length == 1) {
		if(!child[0].childElementCount) {
			var loadAction = $(this).jzURL("SitemapApplication.loadChild");
			var param = {};
			var id = $(this).attr("id");
			param["id"] = id.substr(0,id.indexOf("-"));
			var result;
			var ajaxGet = $.ajax({
				type: "get",
				data: param,
			    url: loadAction,
			    async: false,
			    dataType: "html",
			    success: function(data) {
			    	var branches = $(data).appendTo(child[0]);
					$("#tree").treeview({
						add: branches
					});
			    }
			});
		}
	}
};

$(function() {
	$("#tree").treeview({
		collapsed : true,
		animated : "fast",
		control : "#sidetreecontrol",
		prerendered : true,
		persist : "location"
	});
	
	$(".expandable").each(function() {
		$(this).one("click", juzu.Sitemap.lazyLoad);
	});
})