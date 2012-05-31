/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.gatein.portal.samples.api;

import org.gatein.api.GateIn;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Site;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/** @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a> */
public class NavigationPortlet extends GenericPortlet
{
   private GateIn gateIn;

   @Override
   public void init(PortletConfig config) throws PortletException
   {
      super.init(config);
      gateIn = (GateIn)config.getPortletContext().getAttribute(GateIn.GATEIN_API);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      PrintWriter writer = response.getWriter();

      writer.println("<h1>Sites</h1>");
      List<Site> sites = gateIn.getSites();
      for (Site site : sites)
      {
         outputSite(site, writer);
      }

      writer.println("<h1>Spaces</h1>");
      List<Site> spaces = gateIn.getSites(Site.Type.SPACE);
      for (Site space : spaces)
      {
         outputSite(space, writer);
      }

      writer.println("<h1>Dashboard</h1>");
      outputSite(gateIn.getSite(Site.Type.DASHBOARD, "root"), writer);
   }

   private void outputSite(Site site, PrintWriter writer) throws IOException
   {
      Navigation navigation = site.getNavigation();

      Collection<? extends Navigation> adminNodes = navigation.getChildren();

      writer.println("<h2>" + site.getDisplayName() + "</h2>");
      writer.println("<ul>");

      for (Navigation adminNode : adminNodes)
      {
         outputNode(adminNode, writer);
      }

      writer.println("</ul><br/>");
   }

   private void outputNode(Navigation node, PrintWriter writer)
   {
      Collection<? extends Navigation> children = node.getChildren();
      int size = children.size();
      boolean isLeaf = size == 0;
      writer.println("<li>"
         + (isLeaf ? "<a style='font-weight: bold; text-decoration: underline; color: #336666;' href='" + node.getURI() + "'>" : "")
         + node.getDisplayName()
         + (isLeaf ? "</a>" : "")
         + "</li>");
      if (size != 0)
      {
         writer.println("<ul>");
         for (Navigation child : children)
         {
            outputNode(child, writer);
         }
         writer.println("</ul>");
      }
   }
}
