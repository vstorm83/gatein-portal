/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.portal.mop.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.PersistentApplicationState;
import org.exoplatform.portal.mop.AbstractMopServiceTest;
import org.exoplatform.portal.pom.data.ApplicationData;
import org.exoplatform.portal.pom.data.ComponentData;
import org.exoplatform.portal.pom.data.ContainerAdapter;
import org.exoplatform.portal.pom.data.ContainerData;
import org.gatein.mop.core.util.Tools;
import org.gatein.portal.mop.Properties;
import org.gatein.portal.mop.customization.CustomizationContext;
import org.gatein.portal.mop.customization.CustomizationService;
import org.gatein.portal.mop.hierarchy.NodeContext;
import org.gatein.portal.mop.hierarchy.NodeData;
import org.gatein.portal.mop.layout.Element;
import org.gatein.portal.mop.layout.ElementState;
import org.gatein.portal.mop.layout.LayoutService;
import org.gatein.portal.mop.page.PageData;
import org.gatein.portal.mop.page.PageState;
import org.gatein.portal.mop.site.SiteData;
import org.gatein.portal.mop.site.SiteType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class TestLayout extends AbstractMopServiceTest {

    /** . */
    private static final ElementState.WindowBuilder BAR_PORTLET = Element.portlet("app/bar").title("bar");

    /** . */
    private static final ElementState.WindowBuilder FOO_PORTLET = Element.portlet("app/foo").
            title("foo_title").
            description("foo_description").
            accessPermissions("foo_access_permissions").
            icon("foo_icon").
            showApplicationMode(true).
            showApplicationState(true).
            showInfoBar(false).
            theme("foo_theme").
            width("foo_width").
            height("foo_height");

    /** . */
    private LayoutService layoutService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //
        this.layoutService = context.getLayoutService();
    }

    public void testSite() {
        SiteData site = createSite(SiteType.PORTAL, "test_layout_site");
        createElements(site, FOO_PORTLET, BAR_PORTLET);
        String layoutId = site.layoutId;
        testAll(layoutId);
        context.getSiteService().destroySite(site.key);
        assertEmptyLayout(layoutId);

    }

    public void testUpdateLayout() {
        PageData page = createPage(createSite(SiteType.PORTAL, "test_layout_page"), "page", new PageState.Builder().build());
        createElements(page, FOO_PORTLET, BAR_PORTLET);
        String layoutId = page.layoutId;

        NodeContext<Element, ElementState> pageContext = layoutService.loadLayout(Element.MODEL, layoutId, null);
        assertEquals(2, pageContext.getNodeSize());
        Element foo = pageContext.getNode(0);
        Element bar = pageContext.getNode(1);
        testInfo(foo, bar);

        //Load layout tree
        NodeContext<ComponentData, ElementState> pageStruct = (NodeContext<ComponentData, ElementState>)layoutService.loadLayout(ElementState.model(), layoutId, null);
        assertEquals(2, pageStruct.getNodeSize());

        Iterator<NodeContext<ComponentData, ElementState>> iterator = pageStruct.iterator();

        //Window 1
        NodeContext<ComponentData, ElementState> win1 = iterator.next();
        ElementState.Window win1State = (ElementState.Window) win1.getState();
        ApplicationData app1 = new ApplicationData(win1.getId(), win1.getName(), win1State.type, win1State.state, win1.getId(), win1State.properties.get(ElementState.Window.TITLE), win1State.properties.get(ElementState.Window.ICON), win1State.properties.get(ElementState.Window.DESCRIPTION), win1State.properties.get(ElementState.Window.SHOW_INFO_BAR), win1State.properties.get(ElementState.Window.SHOW_APPLICATION_STATE), win1State.properties.get(ElementState.Window.SHOW_APPLICATION_MODE), win1State.properties.get(ElementState.Window.THEME), win1State.properties.get(ElementState.Window.WIDTH), win1State.properties.get(ElementState.Window.HEIGHT), Collections.EMPTY_MAP, win1State.accessPermissions);

        //Window 2
        NodeContext<ComponentData, ElementState> win2 = iterator.next();
        ElementState.Window win2State = (ElementState.Window) win2.getState();
        ApplicationData app2 = new ApplicationData( win2.getId(), win2.getName(), win2State.type, win2State.state, win2.getId(), win2State.properties.get(ElementState.Window.TITLE), win2State.properties.get(ElementState.Window.ICON), win2State.properties.get(ElementState.Window.DESCRIPTION), false, false, false, win2State.properties.get(ElementState.Window.THEME), win2State.properties.get(ElementState.Window.WIDTH), win2State.properties.get(ElementState.Window.HEIGHT), Collections.EMPTY_MAP, win2State.accessPermissions);

        //Rebuild children with barPortlet before fooPortlet
        List<ComponentData> childrens = new LinkedList<ComponentData>();
        childrens.add(app2);
        childrens.add(app1);

        //Rebuild tree
        ElementState.Container rootState = (ElementState.Container) pageStruct.getState();
        ContainerData rootData = new ContainerData( pageStruct.getId(), pageStruct.getName(), pageStruct.getId(), rootState.getProperties().get(ElementState.Container.NAME), rootState.getProperties().get(ElementState.Container.ICON), rootState.getProperties().get(ElementState.Container.TEMPLATE), rootState.getProperties().get(ElementState.Container.FACTORY_ID), rootState.getProperties().get(ElementState.Container.TITLE), rootState.getProperties().get(ElementState.Container.DESCRIPTION), rootState.getProperties().get(ElementState.Container.WIDTH), rootState.getProperties().get(ElementState.Container.HEIGHT), rootState.getAccessPermissions(), childrens);

        layoutService.saveLayout(new ContainerAdapter(rootData), rootData, pageStruct, null);


        pageContext = layoutService.loadLayout(Element.MODEL, layoutId, null);
        assertEquals(2, pageContext.getNodeSize());
        bar = pageContext.getNode(0);
        foo = pageContext.getNode(1);
        testInfo(foo, bar);
    }


    private void testInfo(Element foo, Element bar) {
        if (foo == null || bar == null) {
            fail("Foo and Bar component must no be null");
        }
        ElementState.Window fooWindow = (ElementState.Window) foo.getState();
        assertEquals(Collections.singletonList("foo_access_permissions"), fooWindow.accessPermissions);
        assertEquals("foo_title", fooWindow.properties.get(ElementState.Window.TITLE));
        assertEquals("foo_description", fooWindow.properties.get(ElementState.Window.DESCRIPTION));
        assertEquals("foo_theme", fooWindow.properties.get(ElementState.Window.THEME));
        assertEquals("foo_height", fooWindow.properties.get(ElementState.Window.HEIGHT));
        assertEquals("foo_width", fooWindow.properties.get(ElementState.Window.WIDTH));

        assertTrue(foo.getState() instanceof ElementState.Window);
        ApplicationState fooState = ((ElementState.Window) foo.getState()).state;
        assertTrue(fooState instanceof PersistentApplicationState);

        CustomizationService cusService = context.getCustomizationService();
        PersistentApplicationState pState = (PersistentApplicationState)fooState;
        CustomizationContext customContext = cusService.loadCustomization(pState.getStorageId());
        assertNotNull(customContext);
        assertNotNull(customContext.getContentId());

        assertEquals("bar", ((ElementState.Window) bar.getState()).properties.get(ElementState.Window.TITLE));
    }

    public void testPage() {
        PageData page = createPage(createSite(SiteType.PORTAL, "test_layout_page"), "page", new PageState.Builder().build());
        createElements(page, FOO_PORTLET, BAR_PORTLET);
        String layoutId = page.layoutId;
        testAll(layoutId);
        context.getPageService().destroyPage(page.key);
        assertEmptyLayout(layoutId);
    }
    
    public void testSwitchLayout() {
        PageData page = createPage(createSite(SiteType.PORTAL, "test_layout_page"), "page", new PageState.Builder().build());
        NodeData<ElementState>[] containers = createElements(page, Element.container(), Element.container(), Element.container());
        createElements(context.getLayoutStore().begin(page.layoutId, true), containers[0], FOO_PORTLET);
        createElements(context.getLayoutStore().begin(page.layoutId, true), containers[1], BAR_PORTLET);
        createElements(context.getLayoutStore().begin(page.layoutId, true), containers[2], FOO_PORTLET);

        String layoutId = page.layoutId;

        NodeContext<ComponentData, ElementState> pageStruct = (NodeContext<ComponentData, ElementState>) layoutService
                .loadLayout(ElementState.model(), layoutId, null);        
        
        Iterator<NodeContext<ComponentData, ElementState>> iterator = pageStruct.iterator();        
        NodeContext<ComponentData, ElementState> cont1 = iterator.next();
        String cont1Name = cont1.getName();
        NodeContext<ComponentData, ElementState> cont2 = iterator.next();
        NodeContext<ComponentData, ElementState> cont3 = iterator.next();
        NodeContext<ComponentData, ElementState> app1 = cont1.iterator().next();
        NodeContext<ComponentData, ElementState> app2 = cont2.iterator().next();
        NodeContext<ComponentData, ElementState> app3 = cont3.iterator().next();
        
        pageStruct.add(0, cont3);
        layoutService.saveLayout(pageStruct, null); 
        
        pageStruct = (NodeContext<ComponentData, ElementState>) layoutService
                .loadLayout(ElementState.model(), layoutId, null);
        ComponentData app1D = buildComponentData(find(app1.getName(), pageStruct));
        ComponentData app2D = buildComponentData(find(app2.getName(), pageStruct));
        ComponentData app3D = buildComponentData(find(app3.getName(), pageStruct));
        
        //Switch from 3 zones layout to 1 zone layout
        ComponentData con1Data = buildComponentData(find(cont1Name, pageStruct), app1D, app2D, app3D);
        ComponentData pageData = buildComponentData(pageStruct, con1Data);
        layoutService.saveLayout(new ContainerAdapter((ContainerData)pageData), pageData, pageStruct, null);
        
        pageStruct = (NodeContext<ComponentData, ElementState>) layoutService
                .loadLayout(ElementState.model(), layoutId, null);
        CustomizationService cusService = context.getCustomizationService();
        assertNotNull(cusService.loadCustomization(find(app3.getName(), pageStruct).getId()));
    }
    
    private <T extends ComponentData> T buildComponentData(NodeContext<ComponentData, ElementState> context, ComponentData ...childs) {
        ElementState state = context.getState();
        if (state instanceof ElementState.Container) {
            List<ComponentData> children = new ArrayList<ComponentData>();
            if (childs != null) {
                for (ComponentData c : childs) {
                    children.add(c);
                }
            }
            ElementState.Container containerState = (ElementState.Container) state;
            Properties properties = containerState.properties;
            ContainerData containerData = new ContainerData(context.getId(), context.getName(), context.getId(),
                    properties.get(ElementState.Container.NAME), properties.get(ElementState.Container.ICON),
                    properties.get(ElementState.Container.TEMPLATE), properties.get(ElementState.Container.FACTORY_ID),
                    properties.get(ElementState.Container.TITLE), properties.get(ElementState.Container.DESCRIPTION),
                    properties.get(ElementState.Container.WIDTH), properties.get(ElementState.Container.HEIGHT),
                    containerState.getAccessPermissions(), children);

            return (T) containerData;
        } else if (state instanceof ElementState.Window) {
            ElementState.Window winState = (ElementState.Window) state;
            Properties properties = winState.properties;
            ApplicationState appState = winState.state;
            ApplicationData appData = new ApplicationData(context.getId(), context.getName(), winState.type, appState,
                    context.getId(), properties.get(ElementState.Window.TITLE), properties.get(ElementState.Window.ICON),
                    properties.get(ElementState.Window.DESCRIPTION), false,
                    false,
                    false, properties.get(ElementState.Window.THEME),
                    properties.get(ElementState.Window.WIDTH), properties.get(ElementState.Window.HEIGHT),
                    Collections.EMPTY_MAP, winState.accessPermissions);

            return (T) appData;
        }
        return null;
    }

    private NodeContext<ComponentData, ElementState> find(String id, NodeContext<ComponentData, ElementState> target) {
        if (target.getName().equals(id)) {
            return target;
        } else {
            for (NodeContext<ComponentData, ElementState> child : target) {
                NodeContext<ComponentData, ElementState> tmp = this.find(id, child);
                if (tmp != null) {
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * One single test now that do multiple things : shorcut
     */
    private void testAll(String layoutId) {

        //
        NodeContext<Element, ElementState> context = layoutService.loadLayout(Element.MODEL, layoutId, null);
        assertEquals(2, context.getNodeSize());
        Element foo = context.getNode(0);
        Element bar = context.getNode(1);
        ElementState.Window fooWindow = (ElementState.Window) foo.getState();
        assertEquals(Collections.singletonList("foo_access_permissions"), fooWindow.accessPermissions);
        assertEquals("foo_title", fooWindow.properties.get(ElementState.Window.TITLE));
        assertEquals("foo_description", fooWindow.properties.get(ElementState.Window.DESCRIPTION));
        assertEquals("foo_theme", fooWindow.properties.get(ElementState.Window.THEME));
        assertEquals("foo_height", fooWindow.properties.get(ElementState.Window.HEIGHT));
        assertEquals("foo_width", fooWindow.properties.get(ElementState.Window.WIDTH));
        assertEquals("bar", ((ElementState.Window) bar.getState()).properties.get(ElementState.Window.TITLE));

        // Add a new portlet in the background
        createElements(layoutId, Element.portlet("app/juu").title("juu"));

        // Save with no changes but we get the concurrent change
        layoutService.saveLayout(context, null);
        assertEquals(3, context.getNodeSize());
        foo = context.getNode(0);
        bar = context.getNode(1);
        Element juu = context.getNode(2);
        assertEquals("foo_title", ((ElementState.Window) foo.getState()).properties.get(ElementState.Window.TITLE));
        assertEquals("bar", ((ElementState.Window) bar.getState()).properties.get(ElementState.Window.TITLE));
        assertEquals("juu", ((ElementState.Window) juu.getState()).properties.get(ElementState.Window.TITLE));

        // Test move
        context.add(1, context.get(2));
        layoutService.saveLayout(context, null);
        assertEquals(3, context.getNodeSize());
        foo = context.getNode(0);
        juu = context.getNode(1);
        bar = context.getNode(2);
        assertEquals("foo_title", ((ElementState.Window) foo.getState()).properties.get(ElementState.Window.TITLE));
        assertEquals("juu", ((ElementState.Window) juu.getState()).properties.get(ElementState.Window.TITLE));
        assertEquals("bar", ((ElementState.Window) bar.getState()).properties.get(ElementState.Window.TITLE));

        //
        NodeData<ElementState> root = getElement(layoutId, layoutId);
        assertEquals(Arrays.asList(context.get(0).getId(), context.get(1).getId(), context.get(2).getId()), Tools.list(root.iterator()));

        // Test update
        context.getNode(0).setState(((ElementState.WindowBuilder)context.getNode(0).getState().builder()).description("foodesc").build());
        layoutService.saveLayout(context, null);

        //
        assertEquals("foodesc", ((ElementState.Window)getElement(layoutId, context.getNode(0).getId()).getState()).properties.get(ElementState.Window.DESCRIPTION));

        // Test destroy
        assertTrue(context.get(0).removeNode());
        layoutService.saveLayout(context, null);

        //
        root = getElement(layoutId, layoutId);
        assertEquals(Arrays.asList(context.get(0).getId(), context.get(1).getId()), Tools.list(root.iterator()));
    }

    private void assertEmptyLayout(String layoutId) {
        NodeContext<Element, ElementState> context = layoutService.loadLayout(Element.MODEL, layoutId, null);
        if (context != null) {
            assertEquals(0, context.getSize());
        }
    }
}
