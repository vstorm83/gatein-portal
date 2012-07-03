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

package org.gatein.portal.api.impl.portal;

import org.gatein.api.commons.Filter;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.portal.api.impl.AbstractAPITestCase;

import java.util.List;

/** @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a> */
public class PageTestCase extends AbstractAPITestCase
{
   public void testGetPages_WithFilter()
   {
      Site fooSite = gatein.addSite(Site.Type.SITE, "foo");
      fooSite.createPage("page1");
      fooSite.createPage("page2");
      fooSite.createPage("page3");
      fooSite.createPage("page4");
      fooSite.createPage("page5");
      fooSite.createPage("page6");
      fooSite.createPage("page7");
      fooSite.createPage("page8");
      fooSite.createPage("page9");
      fooSite.createPage("page10");

      List<Page> pages = fooSite.getPages(new Filter<Page>()
      {
         @Override
         public boolean accept(Page page)
         {
            String name = page.getName();
            int number = getNumber(name);

            return (number % 2 == 0);
         }

         private int getNumber(String name)
         {
            if (name.length() > 5)
            {
               return Integer.parseInt(name.substring(4, 6));
            }
            else
            {
               return Integer.parseInt(name.substring(4, 5));
            }
         }
      });

      assertEquals(5, pages.size());
   }

}
