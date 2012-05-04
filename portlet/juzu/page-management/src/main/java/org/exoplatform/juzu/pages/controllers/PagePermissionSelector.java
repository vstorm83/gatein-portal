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
import org.exoplatform.services.organization.OrganizationService;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class PagePermissionSelector
{

   @Inject
   OrganizationService orgService;
   
   private String buildTree() throws Exception
   {
      StringBuilder b = new StringBuilder();
      boolean lastExpandable = false;
      List<Group> groups  = new ArrayList<Group>(orgService.getGroupHandler().findGroups(null));
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
            .append("</div>");
            
            b.append("<span>").append(group.getLabel()).append("</span>");
            b.append("<ul style='display: none;'>");
            travel(group, b);
            b.append("</ul>");
            b.append("</li>");
         } else {
            b.append("<li").append(lastExpandable ? " class='last'>" : ">");
            b.append("<span>").append(group.getLabel()).append("</span>").append("</li>");
         }
      }
      return b.toString();
   }
   
   private void travel(Group group, StringBuilder b) 
   {
      
   }
   
   private boolean hasChild(Group group) throws Exception
   {
      return orgService.getGroupHandler().findGroups(group).size() > 0;
   }
   
   private void foo() throws Exception {
      Collection<Group> groups  =orgService.getGroupHandler().findGroups(null);
      System.out.println(groups);
//      System.out.println(orgService.getMembershipTypeHandler().findMembershipTypes());
      for(Group group : groups) 
      {
         System.out.println(group.getId() + ":" + group.getLabel());
         System.out.println("parent: " + group.getGroupName() + " >> " + orgService.getGroupHandler().findGroups(group));
         System.out.println(orgService.getGroupHandler().findGroups(group).size());
      }
   }
}
