@Application(defaultController = org.exoplatform.juzu.pages.controllers.PageManagementController.class)
@Assets(scripts = {@org.juzu.plugin.asset.Script(src = "public/javascripts/jquery-1.7.1.min.js"),
   @Script(src = "public/javascripts/jquery.form.js"),
   @Script(src = "public/javascripts/pages.js")}, stylesheets = {
   @Stylesheet(src = "public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "public/stylesheets/bootstrap-responsive.css")})
package org.exoplatform.juzu.pages;
import org.juzu.plugin.asset.Stylesheet;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Assets;
import org.juzu.Application;
