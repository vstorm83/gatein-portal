@Application(defaultController = org.exoplatform.juzu.navigation.controllers.DefaultController.class)
@Portlet

@Assets(
   scripts = {
      @Script(id = "jquery", src = "public/javascripts/jquery-1.7.1.min.js"),
      @Script(src = "public/javascripts/jquery.treeview.js", depends="jquery"),
      @Script(src = "public/javascripts/jquery.treeview.edit.js", depends="jquery"),
      @Script(src = "public/javascripts/bootstrap-transition.js", depends="jquery"),
      @Script(src = "public/javascripts/bootstrap-collapse.js", depends="jquery"),
      @Script(src = "public/javascripts/bootstrap-carousel.js", depends="jquery"),
      @Script(src = "public/javascripts/navigation.js", depends="juzu.ajax")
   },
   stylesheets = {
      @Stylesheet(src = "public/bootstrap.css"),
      @Stylesheet(src = "public/jquery.treeview.css"),
      @Stylesheet(src = "public/navigation.css")
   }
)

package org.exoplatform.juzu.navigation;

import juzu.Application;
import juzu.plugin.portlet.Portlet;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
