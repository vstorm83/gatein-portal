@Application(defaultController = org.exoplatform.juzu.pages.controllers.PageManagement.class)
@Portlet
@Assets(scripts = {
   @Script(id = "jquery", src = "public/javascripts/libs/jquery-1.7.1.min.js"),
   @Script(src = "public/javascripts/libs/jquery.form.js", depends="jquery"),
   @Script(src = "public/javascripts/libs/jquery.treeview.js", depends="jquery"),
   @Script(src = "public/javascripts/libs/bootstrap-transition.js", depends="jquery"),
   @Script(src = "public/javascripts/libs/bootstrap-modal.js", depends="jquery"),
   @Script(src = "public/javascripts/libs/bootstrap-tab.js", depends="jquery"),
   @Script(src = "public/javascripts/libs/bootstrap-alert.js", depends="jquery"),
   @Script(src = "public/javascripts/pages.js", depends="juzu.ajax"),
   @Script(src = "public/javascripts/modal.js", depends="juzu.ajax"),
   @Script(src = "public/javascripts/settings.js", depends="juzu.ajax"),
   @Script(src = "public/javascripts/layout.js", depends="juzu.ajax"),
   @Script(src = "public/javascripts/selector.js", depends="juzu.ajax")}, 
   stylesheets = {
   @Stylesheet(src = "public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "public/stylesheets/bootstrap-responsive.css"),
   @Stylesheet(src = "public/jquery.treeview.css")})
@Bindings({
   @Binding(value = org.exoplatform.portal.config.UserPortalConfigService.class, implementation = GateProviderFactory.class),
   @Binding(value = org.exoplatform.portal.config.DataStorage.class, implementation = GateProviderFactory.class),
   @Binding(value = org.exoplatform.portal.config.UserACL.class, implementation = GateProviderFactory.class),
   @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation = GateProviderFactory.class)
})
package org.exoplatform.juzu.pages;
import juzu.plugin.binding.Bindings;
import juzu.plugin.binding.Binding;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Assets;
import juzu.Application;
import juzu.plugin.portlet.Portlet;
