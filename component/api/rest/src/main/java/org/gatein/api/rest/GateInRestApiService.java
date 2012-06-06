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

package org.gatein.api.rest;

import org.gatein.api.GateIn;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.management.api.annotations.Managed;
import org.gatein.management.api.annotations.ManagedModel;
import org.gatein.management.api.annotations.ManagedOperation;
import org.gatein.management.api.annotations.MappedPath;
import org.gatein.management.api.exceptions.ResourceNotFoundException;
import org.gatein.management.api.operation.OperationNames;

import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Managed(value = "api", description = "GateIn REST API")
public class GateInRestApiService
{
   private final GateIn gatein;

   public GateInRestApiService(GateIn gatein)
   {
      this.gatein = gatein;
   }

   //------------------------------------------------- Portal Sites --------------------------------------------------//
   @Managed("/sites")
   public @ManagedModel("sites") List<Site> getSites()
   {
      return gatein.getSites(Site.Type.SITE);
   }

   @Managed("/sites/{site-name}")
   public @ManagedModel("site") Site getSite(@MappedPath("site-name") String siteName)
   {
      return gatein.getSite(Site.Type.SITE, siteName);
   }

   @Managed("/sites/{site-name}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given site")
   public void removeSite(@MappedPath("site-name")  String siteName)
   {
      try
      {
         gatein.removeSite(Site.Type.SITE, siteName);
      }
      catch (EntityNotFoundException e)
      {
         throw new ResourceNotFoundException(e.getMessage());
      }
   }

   @Managed("/sites/{site-name}/pages")
   public @ManagedModel("pages") List<Page> getSitePages(@MappedPath("site-name") String siteName)
   {
      return getSite(siteName).getPages();
   }

   @Managed("/sites/{site-name}/pages/{page-name}")
   public @ManagedModel("page") Page getSitePage(@MappedPath("site-name") String siteName, @MappedPath("page-name") String pageName)
   {
      return getSite(siteName).getPage(pageName);
   }

   //--------------------------------------------- Group Sites (Spaces) ----------------------------------------------//
   @Managed("/spaces")
   public @ManagedModel("sites") List<Site> getSpaces()
   {
      return gatein.getSites(Site.Type.SPACE);
   }

   @Managed("/spaces/{group-name: .*}")
   public @ManagedModel("site") Site getSpace(@MappedPath("group-name") String groupName)
   {
      if (groupName.charAt(0) != '/') groupName = '/' + groupName;

      return gatein.getSite(Site.Type.SPACE, groupName);
   }

   @Managed("/spaces/{group-name: .*}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given space")
   public void removeSpace(@MappedPath("group-name") String groupName)
   {
      if (groupName.charAt(0) != '/') groupName = '/' + groupName;

      try
      {
         gatein.removeSite(Site.Type.SPACE, groupName);
      }
      catch (EntityNotFoundException e)
      {
         throw new ResourceNotFoundException(e.getMessage());
      }
   }

   @Managed("/spaces/{group-name: .*}/pages")
   public @ManagedModel("pages") List<Page> getSpacePages(@MappedPath("group-name") String groupName)
   {
      return getSpace(groupName).getPages();
   }

   @Managed("/spaces/{group-name: .*}/pages/{page-name}")
   public @ManagedModel("page") Page getSpacePage(@MappedPath("group-name") String groupName,
                                                  @MappedPath("page-name") String pageName)
   {
      return getSpace(groupName).getPage(pageName);
   }

   //-------------------------------------------- User Sites (Dashboard) ---------------------------------------------//
   @Managed("/dashboards")
   public @ManagedModel("sites") List<Site> getDashboards()
   {
      return gatein.getSites(Site.Type.SPACE);
   }

   @Managed("/dashboards/{user-name}")
   public @ManagedModel("site") Site getDashboard(@MappedPath("user-name") String userName)
   {
      return gatein.getSite(Site.Type.DASHBOARD, userName);
   }

   @Managed("/dashboards/{user-name}")
   @ManagedOperation(name = OperationNames.REMOVE_RESOURCE, description = "Removes the given dashboard")
   public void removeDashboard(@MappedPath("user-name") String userName)
   {
      try
      {
         gatein.removeSite(Site.Type.DASHBOARD, userName);
      }
      catch (EntityNotFoundException e)
      {
         throw new ResourceNotFoundException(e.getMessage());
      }
   }

   @Managed("/dashboards/{user-name}/pages")
   public @ManagedModel("pages") List<Page> getDashboardPages(@MappedPath("user-name") String userName)
   {
      return getDashboard(userName).getPages();
   }

   @Managed("/dashboards/{user-name}/pages/{page-name}")
   public @ManagedModel("page") Page getDashboardPage(@MappedPath("user-name") String userName,
                                                      @MappedPath("page-name") String pageName)
   {
      return getDashboard(userName).getPage(pageName);
   }
}
