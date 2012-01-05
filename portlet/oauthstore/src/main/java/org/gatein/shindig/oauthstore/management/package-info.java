@Application
@Assets(
   scripts = {
      @Script(src = "js/oauthstore.js")
   },
   stylesheets = {
      @Stylesheet(src = "skin/stylesheet.css")
   }
)
package org.gatein.shindig.oauthstore.management;

import org.juzu.Application;
import org.juzu.plugin.asset.Assets;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Stylesheet;