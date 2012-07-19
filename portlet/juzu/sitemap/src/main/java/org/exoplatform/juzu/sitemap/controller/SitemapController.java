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
package org.exoplatform.juzu.sitemap.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;

import org.exoplatform.juzu.sitemap.Session;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.GenericScope;
import org.exoplatform.portal.mop.navigation.NodeChange;
import org.exoplatform.portal.mop.navigation.NodeChangeQueue;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.navigation.TreeNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;
import juzu.Controller;
import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.plugin.ajax.Ajax;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 */
public class SitemapController extends Controller
{
   @Inject
   @Path("index.gtmpl")
   org.exoplatform.juzu.sitemap.templates.index index;

   private UserNodeFilterConfig NAVIGATION_FILTER_CONFIG;

   private Scope scope;

   @Inject
   Session session;

   @Inject
   private SitemapController(PortletPreferences preferences) throws Exception
   {
      UserNodeFilterConfig.Builder filterConfigBuilder = UserNodeFilterConfig.builder();
      filterConfigBuilder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL);
      filterConfigBuilder.withTemporalCheck();
      NAVIGATION_FILTER_CONFIG = filterConfigBuilder.build();

      int level = Integer.parseInt(preferences.getValue("level", "2"));
      if (level <= 0)
         scope = Scope.ALL;
      else
         scope = GenericScope.treeShape(level);
   }

   @View
   public void index() throws Exception
   {
      loadTreeNodes();
      index.with().controller(this).render();
   }

   @Ajax
   @Resource
   public Response loadChild(String id) throws Exception
   {
      TreeNode node = session.getTreeNode().findNodes(id);
      if(node != null)
      {
         if (node.isExpanded())
            return null;
         UserNode userNode = updateNode(node.getNode());
         if (userNode != null)
         {
            node.setExpanded(true);
            node.setChildren(userNode.getChildren());
         }
         StringBuilder b = new StringBuilder();
         NodeURL nodeURL = WebuiRequestContext.getCurrentInstance().createURL(NodeURL.TYPE);
         travelNode(node, nodeURL, b);
         return Response.ok(b.toString());
      }
      return Response.notFound("Content not found");
   }
   
   private UserNode updateNode(UserNode node) throws Exception
   {
      if (node == null)
      {
         return null;
      }
      UserPortal userPortal = Util.getPortalRequestContext().getUserPortalConfig().getUserPortal();
      NodeChangeQueue<UserNode> queue = new NodeChangeQueue<UserNode>();
      userPortal.updateNode(node, scope, queue);
      for (NodeChange<UserNode> change : queue)
      {
         if (change instanceof NodeChange.Removed)
         {
            UserNode deletedNode = ((NodeChange.Removed<UserNode>)change).getTarget();
            if (hasRelationship(deletedNode, node))
            {
               // Node has been deleted
               return null;
            }
         }
      }
      return node;
   }

   private boolean hasRelationship(UserNode parent, UserNode userNode)
   {
      if (parent.getId().equals(userNode.getId()))
      {
         return true;
      }
      for (UserNode child : parent.getChildren())
      {
         if (hasRelationship(child, userNode))
         {
            return true;
         }
      }
      return false;
   }

   public String treeView() throws Exception
   {
      StringBuilder b = new StringBuilder();
      NodeURL nodeURL = WebuiRequestContext.getCurrentInstance().createURL(NodeURL.TYPE);
      travelNode(session.getTreeNode(), nodeURL, b);
      return b.toString();
   }

   private void travelNode(TreeNode rootNode, NodeURL nodeURL, StringBuilder b) throws Exception
   {
      List<TreeNode> children = rootNode.getChildren();

      int size = 0;
      for (TreeNode child : children)
      {
         size++;
         UserNode node = child.getNode();
         String id = node.getId() + "-" + System.currentTimeMillis();
         String label = node.getEncodedResolvedLabel();
         String actionLink = null;
         if (node.getPageRef() != null)
         {
            nodeURL.setNode(node);
            actionLink = nodeURL.toString();
         }

         if (child.hasChild())
         {
            b.append("<li")
               .append(" id='")
               .append(id)
               .append("'")
               .append(size == children.size() ? " class='expandable lastExpandable'>" : " class='expandable'>")
               .append("<div")
               .append(
                  size == children.size() ? " class='hitarea expandable-hitarea lastExpandable-hitarea'>"
                     : " class='hitarea expandable-hitarea'>").append("</div>");
            if (actionLink != null)
            {
            	if(actionLink.equals(Util.getPortalRequestContext().getRequestURI())) 
            	{
            		UserNode updateNode = updateNode(node);
            		if(updateNode != null)
            			child.setChildren(updateNode.getChildren());
            	}
               b.append("<a href='").append(actionLink).append("'>").append(label).append("</a>");
            }
            else
            {
               b.append("<span>").append(label).append("</span>");
            }
            b.append("<ul style='display: none;'>");
            travelNode(child, nodeURL, b);
            b.append("</ul>");
            b.append("</li>");
            b.append("<script type='text/javascript'>");
            b.append("$('#").append(id).append("').one('click', juzu.Sitemap.lazyLoad);");
            b.append("</script>");
         }
         else
         {
            b.append("<li").append(size == children.size() ? " class='last'>" : ">").append("<a href='")
               .append(actionLink).append("'>").append(label).append("</a>").append("</li>");
         }
      }
   }

   private void loadTreeNodes() throws Exception
   {
      TreeNode treeNode = new TreeNode();

      UserPortal userPortal = Util.getPortalRequestContext().getUserPortalConfig().getUserPortal();
      List<UserNavigation> listNavigations = userPortal.getNavigations();

      List<UserNode> childNodes = new LinkedList<UserNode>();
      for (UserNavigation nav : rearrangeNavigations(listNavigations))
      {
         try
         {
            UserNode rootNode = userPortal.getNode(nav, scope, NAVIGATION_FILTER_CONFIG, null);
            if (rootNode != null)
            {
               childNodes.addAll(rootNode.getChildren());
            }
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
      treeNode.setChildren(childNodes);
      session.setTreeNode(treeNode);
   }

   private List<UserNavigation> rearrangeNavigations(List<UserNavigation> listNavigation)
   {
      List<UserNavigation> returnNavs = new ArrayList<UserNavigation>();

      List<UserNavigation> portalNavs = new ArrayList<UserNavigation>();
      List<UserNavigation> groupNavs = new ArrayList<UserNavigation>();
      List<UserNavigation> userNavs = new ArrayList<UserNavigation>();

      for (UserNavigation nav : listNavigation)
      {
         SiteType siteType = nav.getKey().getType();
         switch (siteType)
         {
            case PORTAL :
               portalNavs.add(nav);
               break;
            case GROUP :
               groupNavs.add(nav);
               break;
            case USER :
               userNavs.add(nav);
               break;
         }
      }

      returnNavs.addAll(portalNavs);
      returnNavs.addAll(groupNavs);
      returnNavs.addAll(userNavs);

      return returnNavs;
   }
}