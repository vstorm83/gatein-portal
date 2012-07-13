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

package org.gatein.api.management.portal;

import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.api.security.SecurityRestriction;
import org.gatein.management.api.PathAddress;
import org.gatein.management.api.annotations.Managed;
import org.gatein.management.api.annotations.ManagedContext;
import org.gatein.management.api.annotations.ManagedOperation;
import org.gatein.management.api.annotations.MappedPath;
import org.gatein.management.api.exceptions.ResourceNotFoundException;
import org.gatein.management.api.model.ModelList;
import org.gatein.management.api.model.ModelObject;
import org.gatein.management.api.model.ModelReference;
import org.gatein.management.api.operation.OperationNames;

import java.util.List;

import static org.gatein.api.security.SecurityRestriction.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Managed
public class PageManagementResource
{
   private final Site site;

   public PageManagementResource(Site site)
   {
      this.site = site;
   }

   @Managed(description = "Retrieves all pages for given site")
   public ModelList getPages(@ManagedContext ModelList list, @ManagedContext PathAddress address)
   {
      // Populate model
      populateModel(site.getPages(), list, address);

      return list;
   }

   @Managed("{page-name}")
   public ModelObject getPage(@MappedPath("page-name") String name, @ManagedContext ModelObject model)
   {
      Page page = site.getPage(name);
      if (page == null) throw new ResourceNotFoundException("Page " + name + " does not exist for site " + site);

      // Populate model
      populateModel(page, model);

      return model;
   }

   @Managed("{page-name}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given page from the portal")
   public void removePage(@MappedPath("page-name") String name)
   {
      try
      {
         site.removePage(name);
      }
      catch (EntityNotFoundException e)
      {
         throw new ResourceNotFoundException("Could not remove page because page " + Page.Id.create(site.getId(), name) + " does not exist.");
      }
   }

   @Managed("{page-name}")
   @ManagedOperation(name = OperationNames.ADD_RESOURCE, description = "Adds the given page to the portal")
   public ModelObject addPage(@MappedPath("page-name") String name, @ManagedContext ModelObject model)
   {
      Page page = site.createPage(name);
      populateModel(page, model);

      return model;
   }

   private void populateModel(List<Page> pages, ModelList list, PathAddress address)
   {
      for (Page page : pages)
      {
         ModelReference pageRef = list.add().asValue(ModelReference.class);
         pageRef.set("name", page.getName());
         pageRef.set("siteType", page.getId().getSiteId().getType().name().toLowerCase());
         pageRef.set("siteName", page.getId().getSiteId().getName());
         pageRef.set(address.append(page.getName()));
      }
   }

   private void populateModel(Page page, ModelObject model)
   {
      model.set("name", page.getName());
      model.set("title", page.getTitle());
      populateModel(page.getSecurityRestriction(Type.EDIT), "edit-permissions", model);
      populateModel(page.getSecurityRestriction(Type.ACCESS), "access-permissions", model);
   }

   private void populateModel(SecurityRestriction restriction, String fieldName, ModelObject model)
   {
      if (restriction != null)
      {
         ModelList list = model.get(fieldName).asValue(ModelList.class);
         for (Entry entry : restriction.getEntries())
         {
            list.add().asValue(ModelObject.class)
               .set("groupId", entry.getGroupId())
               .set("membershipType", entry.getMembershipType());
         }
      }
   }
}
