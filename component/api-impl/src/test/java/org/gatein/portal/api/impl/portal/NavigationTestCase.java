package org.gatein.portal.api.impl.portal;

import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.gatein.api.portal.Dashboard;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.Space;
import org.gatein.portal.api.impl.AbstractAPITestCase;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class NavigationTestCase extends AbstractAPITestCase
{

   public void testEmptyNavigation()
   {
      createSite(SiteType.PORTAL, "classic");

      //
      Site site = gatein.getSite("portal::classic");
      assertNotNull(site);
      assertEquals("portal::classic", site.getId());
      assertEquals("classic", site.getName());

      //
      Navigation rootNav = site.getRootNavigation();
      assertSame(site, rootNav.getPortalObject());
      assertNotNull(rootNav);
      Iterator<? extends Navigation> i = rootNav.getChildren().iterator();
      assertFalse(i.hasNext());
   }

   public void testGetSites()
   {
      createSite(SiteType.PORTAL, "classic");

      List<Site> sites = gatein.getSites();

      assertNotNull(sites);
      assertEquals(1, sites.size());
      assertEquals("classic", sites.get(0).getName());

   }

   public void testSingleNavigation()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "home");
      navService.saveNode(root, null);

      //
      Site site = gatein.getSiteByName("classic");
      Navigation rootNav = site.getRootNavigation();
      Iterator<? extends Navigation> i = rootNav.getChildren().iterator();
      assertTrue(i.hasNext());
      Navigation homeNav = i.next();
      assertSame(site, homeNav.getPortalObject());
      assertEquals("home", homeNav.getName());
      assertSame(homeNav, rootNav.getChild("home"));
      assertEquals(URI.create("/portal/classic/home"), homeNav.getURI());
      assertFalse(i.hasNext());
   }

   public void testPage()
   {
      NodeContext<?> root = createSite(SiteType.PORTAL, "classic");
      root.add(null, "home");
      navService.saveNode(root, null);

      //
      Site site = gatein.getSiteByName("classic");
      Page homePage = site.getPage("homepage");
      assertNotNull(homePage);
      assertEquals("homepage", homePage.getName());

      //
      Navigation rootNav = site.getRootNavigation();
      Navigation homeNav = rootNav.getChild("home");
      assertNull(homeNav.getTargetPage());
      homeNav.setTargetPage(homePage);
      assertSame(homePage, homeNav.getTargetPage());

      //
      homeNav.setTargetPage((Page)null);
      assertNull(null, homeNav.getTargetPage());
   }

   public void testGroupSite()
   {
      createSite(SiteType.GROUP, "/platform/users");
      Space site = gatein.getSpaceByGroup("platform", "users");
      assertNotNull(site);
   }

   public void testDashboardSite()
   {
      createSite(SiteType.USER, "root");
      Dashboard site = gatein.getDashboardByUser("root");
      assertNotNull(site);
   }
}
