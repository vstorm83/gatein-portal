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

import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.management.api.PathAddress;
import org.gatein.management.api.binding.ModelProvider;
import org.gatein.management.api.model.Model;
import org.gatein.management.api.model.ModelList;
import org.gatein.management.api.model.ModelReference;
import org.gatein.management.api.model.ModelValue;

import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PagesModelMapper implements ModelProvider.ModelMapper<List<Page>>
{
   public static final PagesModelMapper INSTANCE = new PagesModelMapper();

   private PagesModelMapper(){}

   @Override
   public List<Page> from(ModelValue value)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ModelValue to(Model model, List<Page> pages)
   {
      ModelList modelPages = model.setEmptyList();
      for (Page page : pages)
      {
         ModelReference pageRef = modelPages.add().asValue(ModelReference.class);
         pageRef.set("name", page.getName());
         pageRef.set("siteType", page.getId().getSiteId().getType().name().toLowerCase());
         pageRef.set("siteName", page.getId().getSiteId().getName());
         Site site = page.getSite();
         PathAddress pageAddress = PathAddress.pathAddress("api", SiteModelMapper.getSiteTypeRef(site), page.getSite().getId().getName(), "pages", page.getName());
         pageRef.set(pageAddress);
      }

      return modelPages;
   }
}
