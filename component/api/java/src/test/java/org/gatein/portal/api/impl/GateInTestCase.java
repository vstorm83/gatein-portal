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

   public void testSpace()
   {
      createSite(SiteType.GROUP, "/platform/users");

      Site space = gatein.getSite(Site.Id.space("platform", "users"));
      assertNotNull(space);
   }

   public void testDashboard()
   {
      createSite(SiteType.USER, "root");
      Site dashboard = gatein.getSite(Site.Id.dashboard("root"));
      assertNotNull(dashboard);
   }


}
