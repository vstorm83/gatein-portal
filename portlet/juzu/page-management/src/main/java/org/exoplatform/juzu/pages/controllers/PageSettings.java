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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.exoplatform.juzu.pages.Utils;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.webui.util.Util;
import org.juzu.Resource;
import org.juzu.Response;
import org.juzu.plugin.ajax.Ajax;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 */
public class PageSettings
{

   private String ACCESS_AREA = "AccessPermissionList";

   private String EDIT_AREA = "CurrentPermission";

   @Inject
   UserPortalConfigService portalConfig;

   @Inject
   UserACL userACL;

   private String ownerType = "portal";

   private String ownerID = "classic";

   private boolean publicMode = false;

   @Ajax
   @Resource
   public Response saveNewPage() throws Exception
   {
      return Response.ok("Success");
   }

   @Ajax
   @Resource
   public Response changeOwnerID(String ownerID) throws Exception
   {
      this.ownerID = ownerID;
      Map<String, String> map = new HashMap<String, String>();
      String groupIdSelected = ownerID.startsWith("/") ? ownerID : "/" + ownerID;
      String exp = "*:" + groupIdSelected;
      map.put(ACCESS_AREA, renderAccessPermission(new String[]{exp}));

      //
      exp = userACL.getMakableMT() + ":" + groupIdSelected;
      map.put(EDIT_AREA, renderEditPermission(exp));
      map.put("public", Boolean.toString(publicMode));
      return Utils.createJSON(map);
   }

   @Ajax
   @Resource
   public Response changeOwnerType(String ownerType) throws Exception
   {
      this.ownerType = ownerType;
      Map<String, String> map = new HashMap<String, String>();
      map.put("PageOwnerID", renderPageOwnerID());
      map.putAll(buildAccessEditPermission());
      map.put("public", Boolean.toString(publicMode));
      return Utils.createJSON(map);
   }

   private Map<String, String> buildAccessEditPermission() throws Exception
   {
      Map<String, String> map = new HashMap<String, String>();
      if ("portal".equals(ownerType))
      {
         map.put(ACCESS_AREA, renderAccessPermission());
         map.put(EDIT_AREA, renderEditPermission());
      }
      else if ("group".equals(ownerType))
      {
         String groupIdSelected = ownerID.startsWith("/") ? ownerID : "/" + ownerID;
         String exp = "*:" + groupIdSelected;
         map.put(ACCESS_AREA, renderAccessPermission(new String[]{exp}));

         //
         exp = userACL.getMakableMT() + ":" + groupIdSelected;
         map.put(EDIT_AREA, renderEditPermission(exp));
      }
      return map;
   }

   private String renderAccessPermission() throws Exception
   {
      return renderAccessPermission(Util.getUIPortal().getAccessPermissions());
   }

   private String renderAccessPermission(String[] permissions) throws Exception
   {
      List<Permission> list = new ArrayList<Permission>();
      for (String exp : permissions)
      {
         if (UserACL.EVERYONE.equals(exp))
         {
            publicMode = true;
            break;
         }
         if (exp.trim().length() < 1)
         {
            continue;
         }
         Permission permission = new Permission();
         permission.setPermissionExpression(exp);
         if (existsPermission(list, permission))
         {
            continue;
         }
         list.add(permission);
      }
      return renderAccessPermission(list);
   }

   private boolean existsPermission(List<?> list, Permission permission) throws Exception
   {
      for (Object ele : list)
      {
         Permission per = (Permission)ele;
         if (per.getExpression().equals(permission.getExpression()))
         {
            return true;
         }
      }
      return false;
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

   private String renderEditPermission() throws Exception
   {
      return renderEditPermission(Util.getUIPortal().getEditPermission());
   }

   private String renderEditPermission(String exp) throws Exception
   {
      Permission permission = new Permission();
      permission.setPermissionExpression(exp);
      return renderEditPermission(permission);
   }

   private String renderEditPermission(Permission permission) throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("<p>Group ID: ").append("<span  style='color: #0088CC;'>").append(permission.getGroupId()).append("</span>").append("</p>");
      b.append("<p>Membership: ").append("<span  style='color: #0088CC;'>").append(permission.getMembership()).append("</span>").append("</p>");
      return b.toString();
   }

   private String renderPageOwnerID() throws Exception
   {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      StringBuilder b = new StringBuilder();
      if ("portal".equals(ownerType))
      {
         ownerID = "classic";
         b.append("<input type='text' name='onwerID' value='").append(prContext.getPortalOwner())
            .append("' readonly='readonly' />");
         return b.toString();
      }
      else if ("group".equals(ownerType))
      {
         List<String> groups = portalConfig.getMakableNavigations(prContext.getRemoteUser(), true);
         ownerID = groups.get(0);
         b.append("<select name='ownerID'>");
         for (String group : groups)
         {
            b.append("<option value='").append(group).append("'>").append(group).append("</option>");
         }
         b.append("</select>");
         return b.toString();
      }
      return null;
   }

   private boolean isPublicMode()
   {
      String[] permissions = Util.getUIPortal().getAccessPermissions();
      for (String exp : permissions)
      {
         if (UserACL.EVERYONE.equals(exp))
         {
            publicMode = true;
            break;
         }
      }
      return publicMode;
   }
}
