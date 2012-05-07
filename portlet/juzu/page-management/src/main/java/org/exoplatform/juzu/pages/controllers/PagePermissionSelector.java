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
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.juzu.Resource;
import org.juzu.Response;
import org.juzu.plugin.ajax.Ajax;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class PagePermissionSelector
{

   @Inject
   OrganizationService orgService;
   
   @Ajax
   @Resource
   public Response renderBreadcrumb(String groupId) throws Exception 
   {
      StringBuilder b = new StringBuilder();
      Group group = orgService.getGroupHandler().findGroupById(groupId);
      travelBreadcrumb(group.getParentId(), b);
      b.append("<li class='active'>")
      .append(group.getLabel())
      .append("</li>");
      return Response.ok(b.toString());
   }
   
   private void travelBreadcrumb(String groupId, StringBuilder b) throws Exception {
      if(groupId == null) return;
      Group group = orgService.getGroupHandler().findGroupById(groupId);
      travelBreadcrumb(group.getParentId(), b);
      b.append("<li><a href='#!")
      .append(group.getId()).append("'>")
      .append(group.getLabel())
      .append("</a><span class='divider'>/</span></li>");
   }
   
   private String buildGroupTree() throws Exception
   {
      StringBuilder b = new StringBuilder();
     travelTreeView(null, b);
      return b.toString();
   }
   
   private String renderMembership() throws Exception
   {
      Collection<MembershipType> collection = orgService.getMembershipTypeHandler().findMembershipTypes();
      StringBuilder b = new StringBuilder();
      boolean hasStarMembership = false;
      for (MembershipType type : collection)
      {
         b.append("<li>").append("<a>").append(type.getName()).append("</a>").append("</li>");
         if("*".equals(type.getName()))
         {
            hasStarMembership = true;
         }
      }
      
      if(!hasStarMembership) b.append("<li><a>*</a></li>");
      return b.toString();
   }
   
   private void travelTreeView(Group parent, StringBuilder b) throws Exception
   {
      boolean lastExpandable = false;
      List<Group> groups  = new ArrayList<Group>(orgService.getGroupHandler().findGroups(parent));
      for(int i = 0; i < groups.size(); i++) 
      {
         Group group = groups.get(i);
         lastExpandable = (i == groups.size() - 1) ? true : false;
         if (hasChild(group))
         {
            b.append("<li")
            .append(lastExpandable ? " class='expandable lastExpandable'>" : " class='expandable'>")
            .append("<div")
            .append(lastExpandable ? " class='hitarea expandable-hitarea lastExpandable-hitarea'>" : " class='hitarea expandable-hitarea'>")
            .append("</div>")
            
            .append("<span")
            .append(" group-id='").append(group.getId()).append("'>")
            .append(group.getLabel()).append("</span>");
            b.append("<ul style='display: none;'>");
            travelTreeView(group, b);
            b.append("</ul>")
            .append("</li>");
         } else {
            b.append("<li")
            .append(lastExpandable ? " class='last'>" : ">")
            .append("<span style='cursor:pointer'")
            .append(" group-id='").append(group.getId()).append("'>")
            .append(group.getLabel()).append("</span>")
            .append("</li>");
         }
      }
   }
   
   private boolean hasChild(Group group) throws Exception
   {
      return orgService.getGroupHandler().findGroups(group).size() > 0;
   }
}
