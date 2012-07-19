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
package org.exoplatform.juzu.navigation.controllers;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.plugin.ajax.Ajax;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.juzu.navigation.Session;
import org.gatein.api.GateIn;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.SiteQuery;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @author <a href="mailto:nguyenanhkien2a@gmail.com">Kien Nguyen</a>
 * @version $Id$
 *
 */
public class DefaultController
{
   @Inject @Path("index.gtmpl") 
   org.exoplatform.juzu.navigation.templates.index index;

   @Inject @Path("navigation.gtmpl") 
   org.exoplatform.juzu.navigation.templates.navigation navGtmpl;
   
   @Inject
   Session session;
   
   @View
   public void index() 
   {
      session.setSite(getGateIn().getDefaultSite());
      
      SiteQuery<Site> sq = getGateIn().createSiteQuery().setType(Site.Type.SITE);
      List<Site> sites = sq.execute();
      index.with().controller(this).sites(sites).render();
   }
   
   public GateIn getGateIn()
   {
      return (GateIn) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(GateIn.class);
   }
   
   public void render(Navigation nav, StringBuilder sb)
   {
      for (Node node : nav)
      {
         render(node, sb);
      }
   }
   
   public void render(Node node, StringBuilder sb)
   {
      int size = node.getChildCount();
      boolean isLeaf = size == 0;

      if (size > 0)
      {
         sb.append("<li ").append(isLeaf ? "class='expandable lastExpandable'>" : "class='expandable'>");
         sb.append("<div ").append(isLeaf ? "class='hitarea expandable-hitarea lastExpandable-hitarea'>" : "class='hitarea expandable-hitarea'>");
         sb.append("</div>");
      }
      else
      {
         sb.append("<li class='last'>");
      }
      
      sb.append("<span id='").append(node.getId().getPathAsString().replace('/', '-')).append("' node-id='").append(node.getId().getPathAsString()).append("'>").append(node.getName()).append("</span>");
      if (size != 0)
      {
         sb.append("<ul style='display: none;'>");
         for (Node child : node)
         {
            render(child, sb);
         }
         sb.append("</ul>");
      }

      sb.append("</li>");
   }
   
   @Ajax
   @Resource
   public void loadNavigation(String nodeId) {
      session.setSite(getGateIn().getSite(Site.Type.SITE, nodeId));
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void moveUp(String nodeId) {
      System.out.println("Not implemented");
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void moveDown(String nodeId) {
      System.out.println("Not implemented");
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void delete(String nodeId) {
      try
      {
         session.getSite().getNavigation(false).removeNode(nodeId);
      }
      catch (EntityNotFoundException e)
      {
         System.out.println("Fail to delete node with error " + e.getMessage());
      }
      
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void copy(String srcId, String destId) {
      Navigation nav = session.getSite().getNavigation(false);
      Node src = nav.getNode(srcId);
      Node dest = nav.getNode(destId);
      dest.addChild(srcId);
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void clone(String srcId, String destId) {
      Navigation nav = session.getSite().getNavigation(false);
      Node src = nav.getNode(srcId);
      Node dest = nav.getNode(destId);
      dest.addChild(srcId);
      navGtmpl.with().controller(this).render();
   }
   
   @Ajax
   @Resource
   public void cut(String srcId, String destId) {
      Navigation nav = session.getSite().getNavigation(false);
      Node src = nav.getNode(srcId);
      Node dest = nav.getNode(destId);
      if (src != null && dest != null)
      {
         dest.addChild(srcId);
         nav.removeNode(srcId);
      }
      else
      {
         System.out.println("Source or Destination node is not found!");
      }
      navGtmpl.with().controller(this).render();
   }
}
