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

import java.util.LinkedList;

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
   
   public static Node moveUp(Node root, String nodeId) {
      Node node = findNode(root, nodeId);
      return moveUp(root, node);
   }
   
   public static Node moveDown(Node root, String nodeId) {
      Node node = findNode(root, nodeId);
      return moveDown(root, node);
   }
   
   public static Node moveUp(Node root, Node node) {
      Node parent = node.getParent();
      Node prevNode = node.previous();
      if(prevNode == null) return root;
      
      LinkedList<Node> children = parent.getChildren();
      LinkedList<Node> holder = new LinkedList<Node>();
      for(Node sel : children) {
         if(sel.equals(prevNode)) {
            holder.addLast(node);
            continue;
         } else if(sel.equals(node)) {
            holder.addLast(prevNode);
            continue;
         }
         holder.addLast(sel);
      }
      parent.getChildren().clear();
      parent.getChildren().addAll(holder);
      return root;
   }
   
   public static Node moveDown(Node root, Node node) {
      Node parent = node.getParent();
      Node nextNode = node.next();
      if(nextNode == null) return root;
      
      LinkedList<Node> children = parent.getChildren();
      LinkedList<Node> holder = new LinkedList<Node>();
      for(Node sel : children) {
         if(sel.equals(node)) {
            holder.addLast(nextNode);
            continue;
         } else if(sel.equals(nextNode)) {
            holder.addLast(node);
            continue;
         }
         holder.addLast(sel);
      }
      parent.getChildren().clear();
      parent.getChildren().addAll(holder);
      return root;
   }
}
