@Application
@Portlet
@Assets(
   scripts = {
      @Script(id = "jquery", src = "/js/jquery.js"),
      @Script(src = "/js/bootstrap-modal.js"),
      @Script(src = "/js/oauthstore.js", depends = "ajax.app")
   },
   stylesheets = {
      @Stylesheet(src = "/css/bootstrap.css"),
      @Stylesheet(src = "/css/stylesheet.css")
   }
)
package org.gatein.shindig.oauthstore.management;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;