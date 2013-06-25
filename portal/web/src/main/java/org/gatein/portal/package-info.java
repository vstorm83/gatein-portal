/**
 * The portal web application.
 */
@Application(defaultController = org.gatein.portal.page.Controller.class)
@Bindings({
    @Binding(PortletAppManager.class),
    @Binding(PortalContainer.class),
    @Binding(LayoutService.class),
    @Binding(CustomizationService.class),
    @Binding(PageService.class),
    @Binding(NavigationService.class),
    @Binding(SiteService.class),
    @Binding(SimpleLayoutFactory.class),
    @Binding(PortletContentProvider.class),
    @Binding(KernelFilter.class)})
@Assets(
    stylesheets = {
        @Stylesheet(src = "bootstrap-2.3.1.min.css")
    },
    scripts = {
        @Script(src = "javascripts/jquery-1.7.1.min.js"),
        @Script(src = "javascripts/jquery-ui-1.10.3.custom.js"),
        @Script(src = "javascripts/underscore.js"),
        @Script(src = "javascripts/backbone.js"),
        @Script(src = "javascripts/edit.js")
})
package org.gatein.portal;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;

import org.exoplatform.container.PortalContainer;
import org.gatein.portal.kernel.KernelFilter;
import org.gatein.portal.layout.SimpleLayoutFactory;
import org.gatein.portal.mop.customization.CustomizationService;
import org.gatein.portal.mop.layout.LayoutService;
import org.gatein.portal.mop.navigation.NavigationService;
import org.gatein.portal.mop.page.PageService;
import org.gatein.portal.mop.site.SiteService;
import org.gatein.portal.page.spi.portlet.PortletContentProvider;
import org.gatein.portal.portlet.PortletAppManager;