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

import java.util.LinkedList;

import javax.inject.Inject;

import juzu.Path;
import juzu.View;

import org.exoplatform.juzu.navigation.Session;
import org.exoplatform.juzu.navigation.models.Node;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class DefaultController
{
   @Inject @Path("index.gtmpl") 
   org.exoplatform.juzu.navigation.templates.index index;
   
   @Inject
   Session session;
   
   @View
   public void index() 
   {
      if(session.getRootNode() == null)session.setRootNode(Node.createMock(15));
      index.with().controller(this).render();
   }
   
   String render() {
      StringBuilder sb = new StringBuilder();
      Node root = session.getRootNode();
      if(root.hasChild()) {
         render(root, sb);
      } else {
         sb.append("<li class='last'>").append(root.getName()).append("</li>");
      }
      return sb.toString();
   }
   
   void render(Node node, StringBuilder sb) {
      LinkedList<Node> children = node.getChildren();
      int length = children.size();
      for(int i = 0; i < length; i++) {
         Node child = children.get(i);
         if(child.hasChild()) {
            sb.append("<li ").append(i == length - 1 ? "class='expandable lastExpandable'>" : "class='expandable'>");
            sb.append("<div ").append(i == length - 1 ? "class='hitarea expandable-hitarea lastExpandable-hitarea'>" : "class='hitarea expandable-hitarea'>");
            sb.append("</div>");
            sb.append("<span>").append(child.getName()).append("</span>");
            sb.append("<ul style='display: none;'>");
            render(child, sb);
            sb.append("</ul>");
            sb.append("</li>");
         } else {
            sb.append("<li ").append(i == length - 1 ? "class='last'>" : ">");
            sb.append("<span>").append(child.getName()).append("</span>");
            sb.append("</li>");
         }
      }
   }
}
