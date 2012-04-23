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

import org.exoplatform.juzu.pages.Session;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.webui.page.PageQueryAccessList;
import org.juzu.Controller;
import org.juzu.Path;
import org.juzu.Resource;
import org.juzu.Response;
import org.juzu.View;
import org.juzu.plugin.ajax.Ajax;
import org.juzu.template.Template;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class PageManagementController extends Controller
{
   @Inject
   @Path("pages.gtmpl")
   org.exoplatform.juzu.pages.templates.pages pages;
   
   @Inject
   @Path("search.gtmpl")
   Template search;
   
   @Inject
   Session session;
   
   @View
   public void index() 
   {
      if(session.getQuery() == null)
      {
         Query<Page> query = new Query<Page>(null, null, Page.class);
         session.setQuery(query);
      }

      PageQueryAccessList listAccess = new PageQueryAccessList(session.getQuery(), 10);
      System.out.println("available page: " + listAccess.getAvailablePage());
      pages.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public Response search(String title, String name, String type) throws Exception {
      Query<Page> query = session.getQuery();
      if(!title.isEmpty()) 
         query.setTitle(title);
      else
         query.setTitle(null);
      
      if(!name.isEmpty()) 
         query.setOwnerId(name);
      else 
         query.setOwnerId(null);
      
      query.setOwnerType(type);
      query.setName("");
      session.setQuery(query);
      return Response.ok(createResponseHtml());
   }
   
   public String createResponseHtml() throws Exception {
      StringBuilder b = new StringBuilder();
      PageQueryAccessList listAccess = new PageQueryAccessList(session.getQuery(), 10);
      for(int i = 1; i <= listAccess.getAvailablePage(); i++) {
         List<Page> pages = listAccess.getPage(i);          
         for(Page page : pages) {
            b.append("<tr>");
            b.append("<td>").append(page.getPageId()).append("</td>");
            b.append("<td>").append(page.getTitle()).append("</td>");
            b.append("<td>").append(Arrays.toString(page.getAccessPermissions())).append("</td>");
            b.append("<td>").append(page.getEditPermission()).append("</td>");
            b.append("<td><a>edit</a> | <a>delete</a></td>");
         b.append("</tr>");
         }
      }
      return b.toString();
   }
}
