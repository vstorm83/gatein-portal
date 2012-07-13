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

package org.gatein.api.management;

import org.gatein.api.GateIn;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.management.portal.NavigationManagementResource;
import org.gatein.api.management.portal.PageManagementResource;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
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

import static org.gatein.api.portal.Site.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Managed(value = "api", description = "GateIn API Management Resource")
public class GateInApiManagementResource
{
   private static final Logger log = LoggerFactory.getLogger("org.gatein.api.management");

   private final GateIn gatein;

   public GateInApiManagementResource(GateIn gatein)
   {
      this.gatein = gatein;
   }

   //------------------------------------------------- Portal Sites --------------------------------------------------//
   @Managed("/sites")
   public ModelList getSites(@ManagedContext ModelList list, @ManagedContext PathAddress address)
   {
      List<Site> sites = gatein.getSites(Type.SITE);
      populateModel(sites, list, address);

      return list;
   }

   @Managed("/sites/{site-name}")
   public ModelObject getSite(@MappedPath("site-name") String siteName, @ManagedContext ModelObject siteModel, @ManagedContext PathAddress address)
   {
      Id id = Site.Id.site(siteName);
      populateModel(id, siteModel, address);

      return siteModel;
   }

   @Managed("/sites/{site-name}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given site")
   public void removeSite(@MappedPath("site-name")  String siteName)
   {
      Id id = Site.Id.site(siteName);
      try
      {
         gatein.removeSite(id);
      }
      catch (EntityNotFoundException e)
      {
         log.error(e.getMessage());
         throw new ResourceNotFoundException("Cannot remove site " + id + " because site does not exist.");
      }
   }

   @Managed("/sites/{site-name}/pages")
   public PageManagementResource getSitePages(@MappedPath("site-name") String siteName)
   {
      Id id = Id.site(siteName);

      return new PageManagementResource(getSite(id, true));
   }

   @Managed("/sites/{site-name}/navigation")
   public NavigationManagementResource getSiteNavigation(@MappedPath("site-name") String siteName)
   {
      Id id = Id.site(siteName);

      return new NavigationManagementResource(getNavigation(id));
   }

   //--------------------------------------------- Group Sites (Spaces) ----------------------------------------------//
   @Managed("/spaces")
   public ModelList getSpaces(@ManagedContext ModelList list, @ManagedContext PathAddress address)
   {
      List<Site> sites = gatein.getSites(Type.SPACE);
      populateModel(sites, list, address);

      return list;
   }

   @Managed("/spaces/{group-name: .*}")
   public ModelObject getSpace(@MappedPath("group-name") String groupName,
                               @ManagedContext ModelObject siteModel,
                               @ManagedContext PathAddress address)
   {
      Id id = Id.space(forGroupName(groupName));
      populateModel(id, siteModel, address);

      return siteModel;
   }

   @Managed("/spaces/{group-name: .*}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given space")
   public void removeSpace(@MappedPath("group-name") String groupName)
   {
      Id id = Id.space(forGroupName(groupName));
      try
      {
         gatein.removeSite(id);
      }
      catch (EntityNotFoundException e)
      {
         log.error(e.getMessage());
         throw new ResourceNotFoundException("Could not remove site for " + id + " because site does not exist.");
      }
   }

   @Managed("/spaces/{group-name: .*}/pages")
   public PageManagementResource getSpacePages(@MappedPath("group-name") String groupName)
   {
      Id id = Id.space(forGroupName(groupName));

      return new PageManagementResource(getSite(id, true));
   }

   @Managed("/spaces/{group-name: .*}/navigation")
   public NavigationManagementResource getSpaceNavigation(@MappedPath("group-name") String groupName)
   {
      Id id = Id.space(forGroupName(groupName));

      return new NavigationManagementResource(getNavigation(id));
   }

   //-------------------------------------------- User Sites (Dashboard) ---------------------------------------------//
   @Managed("/dashboards")
   public ModelList getDashboards(@ManagedContext ModelList list, @ManagedContext PathAddress address)
   {
      List<Site> sites = gatein.getSites(Type.DASHBOARD);
      populateModel(sites, list, address);

      return list;
   }

   @Managed("/dashboards/{user-name}")
   public ModelObject getDashboard(@MappedPath("user-name") String userName, @ManagedContext ModelObject siteModel, @ManagedContext PathAddress address)
   {
      Id id = Site.Id.dashboard(userName);
      populateModel(id, siteModel, address);

      return siteModel;
   }

   @Managed("/dashboards/{user-name}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given dashboard")
   public void removeDashboard(@MappedPath("user-name") String userName)
   {
      Id id = Site.Id.dashboard(userName);
      try
      {
         gatein.removeSite(id);
      }
      catch (EntityNotFoundException e)
      {
         log.error(e.getMessage());
         throw new ResourceNotFoundException("Cannot remove site " + id + " because site does not exist.");
      }
   }

   @Managed("/dashboards/{user-name}/pages")
   public PageManagementResource getDashboardPages(@MappedPath("user-name") String userName)
   {
      Id id = Id.dashboard(userName);

      return new PageManagementResource(getSite(id, true));
   }

   @Managed("/dashboards/{user-name}/navigation")
   public NavigationManagementResource getDashboardNavigation(@MappedPath("user-name") String userName)
   {
      Id id = Id.dashboard(userName);

      return new NavigationManagementResource(getNavigation(id));
   }

   private Site getSite(Id id, boolean require)
   {
      Site site = gatein.getSite(id);
      if (require && site == null) throw new ResourceNotFoundException("Site not found for " + id);

      return site;
   }

   private Navigation getNavigation(Id id)
   {
      Navigation navigation = getSite(id, true).getNavigation(false);
      if (navigation == null) throw new ResourceNotFoundException("Navigation does not exist for site " + id);

      return navigation;
   }

   private void populateModel(Id id, ModelObject siteModel, PathAddress address)
   {
      Site site = getSite(id, true);

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
         pageRef.set(address.append("pages").append(page.getName()));
      }

      // Navigation
      Navigation nav = site.getNavigation(false);
      ModelList navList = siteModel.get("navigation", ModelList.class);
      for (Node child : nav)
      {
         ModelReference navRef = navList.add().asValue(ModelReference.class);
         navRef.set("name", child.getName());
         navRef.set("label", child.getLabel().getValue(true));
         navRef.set(address.append("navigation").append(child.getName()));
      }
   }

   private void populateModel(List<Site> sites, ModelList list, PathAddress address)
   {
      for (Site site : sites)
      {
         ModelReference siteRef = list.add().asValue(ModelReference.class);
         siteRef.set("name", site.getName());
         siteRef.set("type", site.getType().name().toLowerCase());
         siteRef.set(address.append(site.getName()));
      }
   }

   private String forGroupName(String groupName)
   {
      if (groupName.charAt(0) != '/') groupName = '/' + groupName;

      return groupName;
   }
}
