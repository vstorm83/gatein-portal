@Application(defaultController = org.exoplatform.juzu.register.controllers.RegisterController.class)
@Portlet
@Assets(scripts = {
   @Script(id = "jquery", src = "public/javascripts/jquery-1.7.1.min.js"),
   @Script(src = "public/javascripts/jquery.form.js", depends="jquery"),
   @Script(src = "public/javascripts/register.js", depends="juzu.ajax")},
   stylesheets = {
   @Stylesheet(src = "public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "public/stylesheets/bootstrap-responsive.css")})
@Bindings(@Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation = GateProviderFactory.class))
package org.exoplatform.juzu.register;
import juzu.plugin.binding.Bindings;
import juzu.plugin.binding.Binding;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Assets;
import juzu.Application;
import juzu.plugin.portlet.Portlet;

