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

import java.io.Serializable;
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
import org.gatein.portal.mop.customization.CustomizationContext;
import org.gatein.portal.mop.customization.CustomizationService;
import org.gatein.portal.mop.hierarchy.NodeChangeListener;
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
    private static final ElementState.WindowBuilder FOO_PORTLET = Element.portlet("app/foo").title("foo_title")
            .description("foo_description").accessPermissions("foo_access_permissions").icon("foo_icon")
            .showApplicationMode(true).showApplicationState(true).showInfoBar(false).theme("foo_theme").width("foo_width")
            .height("foo_height");

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

        // Load layout tree
        NodeContext<ComponentData, ElementState> pageStruct = (NodeContext<ComponentData, ElementState>) layoutService
                .loadLayout(ElementState.model(), layoutId, null);
        assertEquals(2, pageStruct.getNodeSize());

        Iterator<NodeContext<ComponentData, ElementState>> iterator = pageStruct.iterator();

        // Window 1
        NodeContext<ComponentData, ElementState> win1 = iterator.next();
        ElementState.Window win1State = (ElementState.Window) win1.getState();
        ApplicationData app1 = new ApplicationData(win1.getId(), win1.getName(), win1State.type, win1State.state, win1.getId(),
                win1State.properties.get(ElementState.Window.TITLE), win1State.properties.get(ElementState.Window.ICON),
                win1State.properties.get(ElementState.Window.DESCRIPTION),
                win1State.properties.get(ElementState.Window.SHOW_INFO_BAR),
                win1State.properties.get(ElementState.Window.SHOW_APPLICATION_STATE),
                win1State.properties.get(ElementState.Window.SHOW_APPLICATION_MODE),
                win1State.properties.get(ElementState.Window.THEME), win1State.properties.get(ElementState.Window.WIDTH),
                win1State.properties.get(ElementState.Window.HEIGHT), Collections.EMPTY_MAP, win1State.accessPermissions);

        // Window 2
        NodeContext<ComponentData, ElementState> win2 = iterator.next();
        ElementState.Window win2State = (ElementState.Window) win2.getState();
        ApplicationData app2 = new ApplicationData(win2.getId(), win2.getName(), win2State.type, win2State.state, win2.getId(),
                win2State.properties.get(ElementState.Window.TITLE), win2State.properties.get(ElementState.Window.ICON),
                win2State.properties.get(ElementState.Window.DESCRIPTION), false, false, false,
                win2State.properties.get(ElementState.Window.THEME), win2State.properties.get(ElementState.Window.WIDTH),
                win2State.properties.get(ElementState.Window.HEIGHT), Collections.EMPTY_MAP, win2State.accessPermissions);

        // Rebuild children with barPortlet before fooPortlet
        List<ComponentData> childrens = new LinkedList<ComponentData>();
        childrens.add(app2);
        childrens.add(app1);

        // Rebuild tree
        ElementState.Container rootState = (ElementState.Container) pageStruct.getState();
        ContainerData rootData = new ContainerData(pageStruct.getId(), pageStruct.getName(), pageStruct.getId(), rootState
                .getProperties().get(ElementState.Container.NAME), rootState.getProperties().get(ElementState.Container.ICON),
                rootState.getProperties().get(ElementState.Container.TEMPLATE), rootState.getProperties().get(
                        ElementState.Container.FACTORY_ID), rootState.getProperties().get(ElementState.Container.TITLE),
                rootState.getProperties().get(ElementState.Container.DESCRIPTION), rootState.getProperties().get(
                        ElementState.Container.WIDTH), rootState.getProperties().get(ElementState.Container.HEIGHT),
                rootState.getAccessPermissions(), childrens);

        layoutService.saveLayout(new ContainerAdapter(rootData), rootData, pageStruct, null);

        pageContext = layoutService.loadLayout(Element.MODEL, layoutId, null);
        assertEquals(2, pageContext.getNodeSize());
        bar = pageContext.getNode(0);
        foo = pageContext.getNode(1);
        testInfo(foo, bar);
    }

    public void testRebase() {
        PageData page = createPage(createSite(SiteType.PORTAL, "test_layout_page"), "page", new PageState.Builder().build());
        createElements(page, FOO_PORTLET, BAR_PORTLET);
        String layoutId = page.layoutId;

        NodeContext<ComponentData, ElementState> pageStruct = (NodeContext<ComponentData, ElementState>) layoutService
                .loadLayout(ElementState.model(), layoutId, null);
        Iterator<NodeContext<ComponentData, ElementState>> iterator = pageStruct.iterator();

        // Window 1
        NodeContext<ComponentData, ElementState> win1 = iterator.next();
        ElementState.Window win1State = (ElementState.Window) win1.getState();
        ApplicationData app1 = new ApplicationData(win1.getId(), win1.getName(), win1State.type, win1State.state, win1.getId(),
                null, null, null, false, false, false, null, null, null, Collections.EMPTY_MAP, null);

        // Window 2
        NodeContext<ComponentData, ElementState> win2 = iterator.next();
        ElementState.Window win2State = (ElementState.Window) win2.getState();
        ApplicationData app2 = new ApplicationData(win2.getId(), win2.getName(), win2State.type, win2State.state, win2.getId(),
                null, null, null, false, false, false, null, null, null, Collections.EMPTY_MAP, null);

        List<ComponentData> childrens1 = new LinkedList<ComponentData>();
        childrens1.add(app2);
        childrens1.add(app1);
        childrens1.add(new ContainerData(null, "1", null, null, null, null, null, null, null, null, null, null,
                new ArrayList<ComponentData>()));

        // Change order of app1 and app2
        //Add new container
        ElementState.Container rootState = (ElementState.Container) pageStruct.getState();
        ContainerData rootData1 = new ContainerData(pageStruct.getId(), pageStruct.getName(), pageStruct.getId(), rootState
                .getProperties().get(ElementState.Container.NAME), null, null, rootState.getProperties().get(
                ElementState.Container.FACTORY_ID), null, null, null, null, null, childrens1);
        layoutService.saveLayout(new ContainerAdapter(rootData1), rootData1, pageStruct, null);

        
        //Try to build the first DB structure with only App1 and App2
        //then rebase and see if there are previous changes in DB          
        
        pageStruct = (NodeContext<ComponentData, ElementState>) layoutService.loadLayout(ElementState.model(), layoutId, null);
        iterator = pageStruct.iterator();

        // Window 2
        win2 = iterator.next();
        win2State = (ElementState.Window) win2.getState();
        app2 = new ApplicationData(win2.getId(), win2.getName(), win2State.type, win2State.state, win2.getId(), null, null,
                null, false, false, false, null, null, null, Collections.EMPTY_MAP, null);

        // Window 1
        win1 = iterator.next();
        win1State = (ElementState.Window) win1.getState();
        app1 = new ApplicationData(win1.getId(), win1.getName(), win1State.type, win1State.state, win1.getId(), null, null,
                null, false, false, false, null, null, null, Collections.EMPTY_MAP, null);

        List<ComponentData> childrens2 = new LinkedList<ComponentData>();
        childrens2.add(app1);
        childrens2.add(app2);
        
        ContainerData rootData2 = new ContainerData(pageStruct.getId(), pageStruct.getName(), pageStruct.getId(), rootState
                .getProperties().get(ElementState.Container.NAME), null, null, rootState.getProperties().get(
                ElementState.Container.FACTORY_ID), null, null, null, null, null, childrens2);

        ChangeListener listener = new ChangeListener();
        
        //Expect to have listener called with moved event: app2 has been moved to 0 index
        //                                                      added event: a new container has beend added
        layoutService.rebaseLayout(new ContainerAdapter(rootData2), rootData2, pageStruct, listener);
        assertTrue(!listener.hasChanges());
    }
    
    static class ChangeListener implements NodeChangeListener {
        public int changes = 0;
        
        public boolean hasChanges() {
            return changes > 0;
        }
        
        @Override
        public void onAdd(Object target, Object parent, Object previous) {
            changes++;
        }

        @Override
        public void onCreate(Object target, Object parent, Object previous, String name, Serializable state) {
            changes++;
        }

        @Override
        public void onRemove(Object target, Object parent) {
            changes++;
        }

        @Override
        public void onDestroy(Object target, Object parent) {
            changes++;
        }

        @Override
        public void onRename(Object target, Object parent, String name) {
            changes++;
        }

        @Override
        public void onUpdate(Object target, Serializable state) {
            changes++;
        }

        @Override
        public void onMove(Object target, Object from, Object to, Object previous) {
            changes++;
        }
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
        PersistentApplicationState pState = (PersistentApplicationState) fooState;
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
        assertEquals(Arrays.asList(context.get(0).getId(), context.get(1).getId(), context.get(2).getId()),
                Tools.list(root.iterator()));

        // Test update
        context.getNode(0).setState(
                ((ElementState.WindowBuilder) context.getNode(0).getState().builder()).description("foodesc").build());
        layoutService.saveLayout(context, null);

        //
        assertEquals("foodesc",
                ((ElementState.Window) getElement(layoutId, context.getNode(0).getId()).getState()).properties
                        .get(ElementState.Window.DESCRIPTION));

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
