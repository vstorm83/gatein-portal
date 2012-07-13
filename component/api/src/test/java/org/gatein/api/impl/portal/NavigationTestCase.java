package org.gatein.api.impl.portal;

import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.Scope;
import org.gatein.api.exception.ApiException;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.impl.AbstractAPITestCase;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;

import java.net.URI;
import java.util.Iterator;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class NavigationTestCase extends AbstractAPITestCase
{

   public void testEmptyNavigation()
   {
      createSite(SiteType.PORTAL, "classic");

      //
      Site site = gatein.getSite(Site.Id.site("classic"));
      assertNotNull(site);
      assertEquals("classic", site.getId().getName());

      //
      Navigation rootNav = site.getNavigation(false);
      assertSame(site, rootNav.getSite());
      assertNotNull(rootNav);
      Iterator<Node> i = rootNav.iterator();
      assertFalse(i.hasNext());
   }


   public void testSingleNavigation()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "home");
      navService.saveNode(root, null);

      //
      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation rootNav = site.getNavigation(false);
      Iterator<Node> i = rootNav.iterator();
      assertTrue(i.hasNext());
      Node homeNav = i.next();
      assertEquals("home", homeNav.getName());
      assertSame(homeNav, rootNav.getNode("home"));
      assertEquals(URI.create("/portal/classic/home"), homeNav.getURI());
      assertFalse(i.hasNext());
   }

   public void testMultipleNavigation()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      NodeContext<?> home = root.add(null, "home");
      home.add(null, "home-1").add(null, "home-1-1");
      home.add(null, "home-2");
      navService.saveNode(root, null);

      //
      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Iterator<Node> i = navigation.iterator();
      assertTrue(i.hasNext());

      Node node = i.next();
      assertEquals("home", node.getName());
      assertSame(node, navigation.getNode("home"));
      assertFalse(i.hasNext());

      assertEquals(2, node.getChildCount());
      i = node.iterator();
      assertTrue(i.hasNext());
      node = i.next();
      assertEquals("home-1", node.getName());
      assertSame(node, navigation.getNode("home").getChild("home-1"));
      assertEquals(1, node.getChildCount());
      assertEquals("home-1-1", node.getChild("home-1-1").getName());

      assertTrue(i.hasNext());
      node = i.next();
      assertEquals("home-2", node.getName());
      assertSame(node, navigation.getNode("home").getChild("home-2"));
   }

   public void testGetNode()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar").add(null, "foobar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);

      assertNotNull(navigation.getNode("foo"));
      assertNotNull(navigation.getNode("foo", "bar"));
      assertNotNull(navigation.getNode("foo", "bar", "foobar"));
      assertNull(navigation.getNode("some", "other", "path"));
   }

   public void testAddNode()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node node = navigation.addNode("foo");
      assertNotNull(node);
      assertEquals("foo", node.getName());

      navService.rebaseNode(root, Scope.CHILDREN, null);
      assertNotNull(root.getNode("foo"));
   }

   public void testAddNode_Descendant()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar").add(null, "foobar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node node = navigation.addNode("foo", "bar", "foobar", "newnode");
      assertNotNull(node);
      assertEquals("newnode", node.getName());

      navService.rebaseNode(root, Scope.ALL, null);
      assertNotNull(root.get("foo").get("bar").get("foobar").get("newnode"));
   }

   public void testAddNode_Descendant_NoParent()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar").add(null, "foobar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      try
      {
         navigation.addNode("foo", "bar", "foobar", "not-exist", "newnode");
      }
      catch (EntityNotFoundException e)
      {
         assertEquals(0, navigation.getNode("foo", "bar", "foobar").getChildCount());
      }
   }

   public void testRemoveNode()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);

      assertNotNull(navigation.getNode("foo", "bar"));
      assertTrue(navigation.removeNode("foo"));
      assertNull(navigation.getNode("foo"));
      assertNull(navigation.getNode("foo", "bar"));
      assertEquals(0, navigation.getNodeCount());
   }

   public void testRemoveNode_No_Child()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);

      assertNotNull(navigation.getNode("foo"));
      assertNull(navigation.getNode("foo", "bar"));
      try
      {
         navigation.removeNode("foo", "bar");
         fail("Should not be able to remove a child that doesn't exist");
      }
      catch (EntityNotFoundException e)
      {
         assertEquals(0, navigation.getNode("foo").getChildCount());
      }
   }

   public void testRemoveNode_No_Parent()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);

      assertNotNull(navigation.getNode("foo"));
      assertNull(navigation.getNode("foo", "bar", "foobar"));
      try
      {
         navigation.removeNode("foo", "bar", "foobar");
         fail("Should not be able to remove a child that doesn't exist");
      }
      catch (EntityNotFoundException e)
      {
         assertEquals(0, navigation.getNode("foo").getChildCount());
      }
   }

   public void testNodeCount()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo");
      root.add(null, "bar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      assertNotNull(navigation.getNode("foo"));
      assertNotNull(navigation.getNode("bar"));
      assertEquals(2, navigation.getNodeCount());
   }

   public void testGetChild()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar").add(null, "foobar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node fooNode = navigation.getNode("foo");

      assertNotNull(fooNode.getChild("bar"));
      assertNotNull(fooNode.getChild("bar").getChild("foobar"));
      assertNull(fooNode.getChild("some-child"));
   }

   public void testAddChild()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node node = navigation.getNode("foo");

      // test default values
      assertEquals(Node.Id.create(site.getId(), "foo"), node.getId());
      assertEquals("foo", node.getName());
      assertNull(node.getChild("bar"));
      assertEquals(0, node.getChildCount());
      assertNull(node.getPage());
      assertNull(node.getPageId());
      assertNull(node.getStartPublicationDate());
      assertNull(node.getEndPublicationDate());
      assertNull(node.getIconName());
      assertEquals(Node.Visibility.VISIBLE, node.getVisibility());
      assertEquals("foo", node.getLabel().getValue(true));
   }

   public void testNodeRemove()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node foo = navigation.getNode("foo");

      assertNotNull(foo);
      assertTrue(foo.removeNode());
      assertNull(navigation.getNode("foo"));

      navigation.addNode("foo");
      assertNotNull(navigation.getNode("foo"));
   }

   public void testRemoveChild()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "foo").add(null, "bar");
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node foo = navigation.getNode("foo");

      assertNotNull(foo);
      assertNotNull(foo.getChild("bar"));
      assertNotNull(navigation.getNode("foo", "bar"));

      assertTrue(foo.removeChild("bar"));

      assertNull(foo.getChild("bar"));
      assertNull(navigation.getNode("foo", "bar"));
   }

   public void testNodeLabel()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      NodeContext<?> foo = root.add(null, "foo");
      foo.setState(foo.getState().builder().label("Simple Foo Label").build());
      navService.saveNode(root, null);

      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Navigation navigation = site.getNavigation(false);
      Node fooNode = navigation.getNode("foo");
      assertEquals("Simple Foo Label", fooNode.getLabel().getValue(true));
   }

   //TODO: Test concurrent changes for different navigation contexts.

   public void testPage()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "home");
      navService.saveNode(root, null);

      //
      Site site = gatein.getSite(Site.Type.SITE, "classic");
      Page homePage = site.getPage("homepage");
      assertNotNull(homePage);
      assertEquals("homepage", homePage.getName());

      //
      Navigation rootNav = site.getNavigation(false);
      Node homeNav = rootNav.getNode("home");

      assertNull(homeNav.getPage());
      homeNav.setPageId(homePage.getId());
      assertEquals(homePage.getId(), homeNav.getPage().getId());

      //
      homeNav.setPageId(null);
      assertNull(homeNav.getPage());
      assertNull(homeNav.getPageId());

      try
      {
         homeNav.setPageId(Page.Id.create(Site.Type.SITE, "foo", "bar"));
         fail("Should not be able to set a page that doesn't exist.");
      }
      catch (ApiException e)
      {
         assertNull(homeNav.getPage());
         assertNull(homeNav.getPageId());
      }
   }
}
