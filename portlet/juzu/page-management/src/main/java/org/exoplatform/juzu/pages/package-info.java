@Application(defaultController = org.exoplatform.juzu.pages.controllers.PageManagement.class)
@Assets(scripts = {@org.juzu.plugin.asset.Script(src = "public/javascripts/libs/jquery-1.7.1.min.js"),
   @Script(src = "public/javascripts/libs/jquery.form.js"),
   @Script(src = "public/javascripts/libs/jquery.treeview.js"),
   @Script(src = "public/javascripts/libs/bootstrap-transition.js"),
   @Script(src = "public/javascripts/libs/bootstrap-modal.js"),
   @Script(src = "public/javascripts/libs/bootstrap-tab.js"),
   @Script(src = "public/javascripts/libs/bootstrap-alert.js"),
   @Script(src = "public/javascripts/pages.js"),
   @Script(src = "public/javascripts/modal.js"),
   @Script(src = "public/javascripts/settings.js"),
   @Script(src = "public/javascripts/layout.js"),
   @Script(src = "public/javascripts/selector.js")}, stylesheets = {
   @Stylesheet(src = "public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "public/stylesheets/bootstrap-responsive.css"),
   @Stylesheet(src = "public/jquery.treeview.css")})
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
