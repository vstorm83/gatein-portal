@Application(defaultController = org.exoplatform.juzu.pages.controllers.PageManagement.class)
@Assets(scripts = {
   @Script(id = "jquery", src = "/public/javascripts/libs/jquery-1.7.1.min.js"),
   @Script(src = "/public/javascripts/libs/jquery.form.js", depends="jquery"),
   @Script(src = "/public/javascripts/libs/jquery.treeview.js", depends="jquery"),
   @Script(src = "/public/javascripts/libs/bootstrap-transition.js", depends="jquery"),
   @Script(src = "/public/javascripts/libs/bootstrap-modal.js", depends="jquery"),
   @Script(src = "/public/javascripts/libs/bootstrap-tab.js", depends="jquery"),
   @Script(src = "/public/javascripts/libs/bootstrap-alert.js", depends="jquery"),
   @Script(src = "/public/javascripts/pages.js", depends="ajax.app"),
   @Script(src = "/public/javascripts/modal.js", depends="ajax.app"),
   @Script(src = "/public/javascripts/settings.js", depends="ajax.app"),
   @Script(src = "/public/javascripts/layout.js", depends="ajax.app"),
   @Script(src = "/public/javascripts/selector.js", depends="ajax.app")}, 
   stylesheets = {
   @Stylesheet(src = "/public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "/public/stylesheets/bootstrap-responsive.css"),
   @Stylesheet(src = "/public/jquery.treeview.css")})
@Bindings({
   @Binding(value = org.exoplatform.portal.config.UserPortalConfigService.class, implementation = GateInMetaProvider.class),
   @Binding(value = org.exoplatform.portal.config.DataStorage.class, implementation = GateInMetaProvider.class),
   @Binding(value = org.exoplatform.portal.config.UserACL.class, implementation = GateInMetaProvider.class),
   @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation = GateInMetaProvider.class)
})
package org.exoplatform.juzu.pages;
import org.juzu.plugin.binding.Bindings;
import org.juzu.plugin.binding.Binding;
import org.juzu.plugin.asset.Stylesheet;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Assets;
import org.juzu.Application;
