@Application(defaultController = org.exoplatform.juzu.sitemap.controller.SitemapController.class)
@Assets(
	scripts = {
		@Script(id = "jquery", src = "/public/javascripts/jquery-1.7.1.min.js"),
		@Script(src = "/public/javascripts/jquery.treeview.js", depends="jquery"),
		@Script(src = "/public/javascripts/jquery.treeview.edit.js", depends="jquery"),
		@Script(src = "/public/javascripts/sitemap.js", depends="ajax.app")},
   stylesheets = {
   	@Stylesheet(src = "/public/jquery.treeview.css")})
package org.exoplatform.juzu.sitemap;
import org.juzu.Application;
import org.juzu.plugin.asset.Stylesheet;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Assets;