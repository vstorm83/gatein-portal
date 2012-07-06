@Application(defaultController = org.exoplatform.juzu.sitemap.controller.SitemapController.class)
@Portlet
@Assets(
	scripts = {
		@Script(id = "jquery", src = "public/javascripts/jquery-1.7.1.min.js"),
		@Script(src = "public/javascripts/jquery.treeview.js", depends="jquery"),
		@Script(src = "public/javascripts/jquery.treeview.edit.js", depends="jquery"),
		@Script(src = "public/javascripts/sitemap.js", depends="juzu.ajax")},
   stylesheets = {
   	@Stylesheet(src = "public/jquery.treeview.css")})
package org.exoplatform.juzu.sitemap;
import juzu.Application;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Assets;
import juzu.plugin.portlet.Portlet;