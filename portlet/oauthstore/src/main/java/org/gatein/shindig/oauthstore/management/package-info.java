@Application
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

import org.juzu.Application;
import org.juzu.plugin.asset.Assets;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Stylesheet;