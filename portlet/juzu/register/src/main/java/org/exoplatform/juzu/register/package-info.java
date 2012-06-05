@Application(defaultController = org.exoplatform.juzu.register.controllers.RegisterController.class)
@Assets(scripts = {
   @Script(id = "jquery", src = "/public/javascripts/jquery-1.7.1.min.js"),
   @Script(src = "/public/javascripts/jquery.form.js", depends="jquery"),
   @Script(src = "/public/javascripts/register.js", depends="ajax.app")},
   stylesheets = {
   @Stylesheet(src = "/public/stylesheets/bootstrap.css"), 
   @Stylesheet(src = "/public/stylesheets/bootstrap-responsive.css")})
@Bindings(@Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation = GateProviderFactory.class))
package org.exoplatform.juzu.register;
import org.juzu.plugin.binding.Bindings;
import org.juzu.plugin.binding.Binding;
import org.juzu.plugin.asset.Stylesheet;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Assets;
import org.juzu.Application;

