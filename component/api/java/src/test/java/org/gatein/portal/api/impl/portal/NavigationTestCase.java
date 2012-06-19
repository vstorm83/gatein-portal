package org.gatein.portal.api.impl.portal;

import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.gatein.api.exception.ApiException;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.portal.api.impl.AbstractAPITestCase;

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
      Navigation rootNav = site.getNavigation();
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
      Navigation rootNav = site.getNavigation();
      Iterator<Node> i = rootNav.iterator();
      assertTrue(i.hasNext());
      Node homeNav = i.next();
      assertEquals("home", homeNav.getName());
      assertSame(homeNav, rootNav.getNode("home"));
      assertEquals(URI.create("/portal/classic/home"), homeNav.getURI());
      assertFalse(i.hasNext());
   }

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
      Navigation rootNav = site.getNavigation();
      Node homeNav = rootNav.getNode("home");

      assertNull(homeNav.getPage());
      homeNav.setPageReference(homePage.getId());
      assertEquals(homePage.getId(), homeNav.getPage().getId());

      //
      homeNav.setPageReference(null);
      assertNull(homeNav.getPage());
      assertNull(homeNav.getPageReference());

      try
      {
         homeNav.setPageReference(Page.Id.create(Site.Type.SITE, "foo", "bar"));
         fail("Should not be able to set a page that doesn't exist.");
      }
      catch (ApiException e)
      {
         assertNull(homeNav.getPage());
         assertNull(homeNav.getPageReference());
      }
   }
}
