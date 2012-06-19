/*
* JBoss, a division of Red Hat
* Copyright 2012, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.gatein.portal.api.impl;

import org.exoplatform.portal.mop.SiteType;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.SiteQuery;

import java.util.List;

/** @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a> */
public class GateInTestCase extends AbstractAPITestCase
{

   public void testGetSites()
   {
      createSite(SiteType.PORTAL, "classic");

      List<Site> sites = gatein.getSites();

      assertNotNull(sites);
      assertEquals(1, sites.size());
      assertEquals("classic", sites.get(0).getId().getName());

   }

   public void testSimplSiteQuery()
   {
      // 4 sites
      gatein.addSite(Site.Type.SITE, "site1");
      gatein.addSite(Site.Type.SITE, "site2");
      gatein.addSite(Site.Type.SITE, "site3");
      gatein.addSite(Site.Type.SITE, "site4");
      gatein.addSite(Site.Type.SPACE, "/platform/users");
      gatein.addSite(Site.Type.SPACE, "/supergroup/coolfolks");
      gatein.addSite(Site.Type.DASHBOARD, "root");
      gatein.addSite(Site.Type.DASHBOARD, "john");
      gatein.addSite(Site.Type.DASHBOARD, "mary");

      List<Site> sites = gatein.getSites();

      assertEquals(9, gatein.getSites().size());

      SiteQuery<Site> sq = gatein.createSiteQuery();

      // Grab all
      assertEquals(9, sq.getResultsCount());


      // Grab SITE
      assertEquals(4, gatein.getSites(Site.Type.SITE).size());
      sq.reset();
      sq.setType(Site.Type.SITE);
      assertEquals(4, sq.getResultsCount());

      // Grab SPACE
      assertEquals(2, gatein.getSites(Site.Type.SPACE).size());
      sq.reset();
      sq.setType(Site.Type.SPACE);
      assertEquals(2, sq.getResultsCount());

      // Grab DASHBOARD
      assertEquals(3, gatein.getSites(Site.Type.DASHBOARD).size());
      sq.reset();
      sq.setType(Site.Type.DASHBOARD);
      assertEquals(3, sq.getResultsCount());

      // Grab single SPACE
      sq.reset();
      sq.setType(Site.Type.SPACE);
      sq.setId(Site.Id.space("/platform/users"));
      System.out.println("###################: " + sq.execute());
      assertEquals(1, sq.getResultsCount());
      assertEquals("/platform/users", sq.execute().get(0).getId().getName());


   }

   public void testAddSite()
   {
      gatein.addSite(Site.Type.SITE, "newsite");

      Site site = gatein.getSite(Site.Type.SITE, "newsite");
      assertNotNull(site);
      assertNull(gatein.getSite(Site.Type.SITE, "xxx"));

   }

   public void testGetSpace()
   {
      createSite(SiteType.GROUP, "/platform/something");

      Site space = gatein.getSite(Site.Id.space("platform", "something"));
      assertNotNull(space);
   }

   public void testGetDashboard()
   {
      createSite(SiteType.USER, "user10");
      Site dashboard = gatein.getSite(Site.Id.dashboard("user10"));
      assertNotNull(dashboard);
   }




}
