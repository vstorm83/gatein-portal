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
package org.exoplatform.juzu.navigation.models;

import java.util.List;


/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class NodeUtil
{
   public static Node findNode(Node root, String nodeId) {
      Node found = null;
      if(nodeId.equals(root.getId())) return root;
      for(Node child : root.getChildren()) {
         found = findNode(child, nodeId);
         if(found != null) {
            break;
         }
      }
      return found;
   }
   
   public static Node createMock(int level) {
      Node root = new Node("root", new Node[] {
         new Node("home", new Node[] {
            new Node("home1"),
            new Node("home2"),
            new Node("home3"),
         }),
         new Node("group", new Node[] {
            new Node("group1"),
            new Node("group2"),
            new Node("group3")
         }),
         
         new Node("empty1"),
         
         new Node("user", new Node[] {
            new Node("user1"),
            new Node("user2"),
            new Node("user3")
         }),
         new Node("empty2")
      });
      
      if(level > 2) {
         List<Node> child = root.getChildren();
         for(Node node : child) {
            for(Node sel : node.getChildren()) {
               foo(sel, level - 2);
            }
         }
      }
      return root;
   }
   
   private static void foo(Node node, int deep) {
      if(deep == 0) return;
      Node added = node.add(new Node(node.getName() + "_1"));
      foo(added, --deep);
   }
}
