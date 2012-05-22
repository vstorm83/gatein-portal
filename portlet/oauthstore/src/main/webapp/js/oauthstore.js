$(function() {
			$('.modal').find('a[href]').live('click', function() {
						var gadgetURI = $(".modal").find('input[type="text"]')
								.val();
						var keyName = $(".modal").find('input[type="hidden"]')
								.val();

						var root = $(this).jz();
						root.find(".uris").jzLoad(
								"OAuthStore.addGadgetURIToKey()", {
									gadgetURI : gadgetURI,
									keyName : keyName
								}, function() {
								});

						$('#myModal').modal('hide');
			})

});