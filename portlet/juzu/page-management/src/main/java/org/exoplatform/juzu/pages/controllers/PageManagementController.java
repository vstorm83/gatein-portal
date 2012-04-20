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

import javax.inject.Inject;

import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.webui.page.PageQueryAccessList;
import org.juzu.Controller;
import org.juzu.Path;
import org.juzu.View;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class PageManagementController extends Controller
{
   @Inject
   @Path("main.gtmpl")
   org.exoplatform.juzu.pages.templates.main main;
   
   private Query<Page> query;
   PageQueryAccessList listAccess;
   
   public PageManagementController() {
      query = new Query<Page>(null, null, Page.class);
      listAccess = new PageQueryAccessList(query, 10);
   }
   
   @View
   public void index() 
   {
      main.with().controller(this).listAccess(listAccess).render();
   }
   
   public String say() {
      StringBuilder b = new StringBuilder();
      b.append("Total size: ").append(listAccess.getAll().size()).append("<br/>");
      b.append("Available: ").append(listAccess.getAvailable()).append("<br/>");
      b.append("Avaliable page: ").append(listAccess.getAvailablePage()).append("<br/>");
      return b.toString(); 
   }
   
   public Page[] getCurrentPage() throws Exception {
      return listAccess.getPage(listAccess.getCurrentPage()).toArray(new Page[] {});
   }
}
