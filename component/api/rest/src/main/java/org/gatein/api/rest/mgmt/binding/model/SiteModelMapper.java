/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.api.rest.mgmt.binding.model;

import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.management.api.PathAddress;
import org.gatein.management.api.binding.ModelProvider;
import org.gatein.management.api.model.Model;
import org.gatein.management.api.model.ModelList;
import org.gatein.management.api.model.ModelObject;
import org.gatein.management.api.model.ModelReference;
import org.gatein.management.api.model.ModelValue;

import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SiteModelMapper implements ModelProvider.ModelMapper<Site>
{
   public static final SiteModelMapper INSTANCE = new SiteModelMapper();

   private SiteModelMapper(){}

   @Override
   public Site from(ModelValue value)
   {
      //TODO: Implement
      throw new UnsupportedOperationException();
   }

   @Override
   public ModelValue to(Model model, Site site)
   {
      ModelObject siteModel = model.setEmptyObject();
      siteModel.set("name", site.getId().getName());
      siteModel.set("type", site.getId().getType().name().toLowerCase());

      // Pages
      ModelList pagesList = siteModel.get("pages", ModelList.class);
      List<Page> pages = site.getPages();
      for (Page page : pages)
      {
         ModelReference pageRef = pagesList.add().asValue(ModelReference.class);
         pageRef.set("name", page.getName());
         pageRef.set("title", page.getTitle());
         pageRef.set(PathAddress.pathAddress("api", getSiteTypeRef(site), site.getId().getName(), "pages", page.getName()));
      }

      // Navigation
      Navigation nav = site.getNavigation();
      ModelList navList = siteModel.get("navigation", ModelList.class);
      for (Node child : nav)
      {
         ModelReference navRef = navList.add().asValue(ModelReference.class);
         navRef.set("name", child.getName());
         navRef.set("label", child.getLabel().getValue());
         navRef.set(PathAddress.pathAddress("api", getSiteTypeRef(site), site.getId().getName(), "navigation", child.getName()));
      }

      return siteModel;
   }

   static String getSiteTypeRef(Site site)
   {
      String siteRef;
      switch (site.getId().getType())
      {
         case SITE:
            siteRef = "sites";
            break;
         case SPACE:
            siteRef = "spaces";
            break;
         case DASHBOARD:
            siteRef = "dashboards";
            break;
         default:
            throw new IllegalArgumentException("Unknown site type " + site.getId().getType() + " for site " + site.getId().getName());
      }
      return siteRef;
   }
}
