/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.juzu.pages.controllers;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.webui.util.Util;
import org.juzu.Controller;
import org.juzu.Resource;
import org.juzu.Response;
import org.juzu.plugin.ajax.Ajax;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 */
public class PageSettingsController extends Controller
{

   @Inject
   UserPortalConfigService portalConfig;
   
   @Inject
   UserACL userACL;
   
   private String ownerType = "portal";
   
   @Ajax
   @Resource
   public Response saveNewPage() throws Exception
   {
      return Response.ok("Success");
   }
   
   @Ajax
   @Resource
   public Response changeOwnerID(String ownerID) throws Exception {
      String groupIdSelected = ownerID.startsWith("/") ? ownerID : "/" + ownerID;
      String exp = "*:" + groupIdSelected;
      Permission permission = new Permission();
      renderAccessPermission(Arrays.asList(permission));
      
      //
      exp = userACL.getMakableMT() + ":" + groupIdSelected;
      permission.setPermissionExpression(exp);
      renderEditPermission(permission);
      return null;
   }
   
   @Ajax
   @Resource
   public Response changeOwnerType(String ownerType) throws Exception {
      this.ownerType = ownerType; 
      return Response.ok(renderPageOwnerID());
   }

  private String renderAccessPermission(List<Permission> list) throws Exception
   {
      StringBuilder b = new StringBuilder();
      for (Permission permission : list)
      {
         b.append("<tr>");
         b.append("<td>").append(permission.getGroupId()).append("</td>");
         b.append("<td>").append(permission.getMembership()).append("</td>");
         b.append("<td>").append("<a>Delete</a>").append("</td>");
         b.append("</tr>");
      }
      return b.toString();
   }

  private String renderEditPermission(Permission permission) throws Exception {
     StringBuilder b = new StringBuilder();
     return b.toString();
  }
   
   private String renderPageOwnerID() throws Exception 
   {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      StringBuilder b = new StringBuilder();
      if("portal".equals(ownerType)) 
      {
         b.append("<input type='text' name='onwerID' value='").append(prContext.getPortalOwner()).append("' readonly='readonly' />");
         return b.toString();
      }
      else if("group".equals(ownerType))
      {
         List<String> groups = portalConfig.getMakableNavigations(prContext.getRemoteUser(), true);
         b.append("<select name='ownerID'>");
         for(String group: groups) 
         {
            b.append("<option value='").append(group).append("'>").append(group).append("</option>");
         }
         b.append("</select>");
         return b.toString();
      }
      return null;
   }
}
