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
public class Node
{
   private String name;
   
   private Node parent;
   
   private LinkedList<Node> children = new LinkedList<Node>();

   public Node(String name) {
      this.name = name;
   }
   
   public Node(String name, Node parent, LinkedList<Node> children) 
   {
      this(name);
      this.parent = parent;
      this.children = children;
   }
   
   public Node(String name, Node parent, Node ... nodes) {
      this(name);
      this.parent = parent;
      add(nodes);
   }
   
   public Node(String name, Node ... nodes) {
      this(name);
      for(Node node : nodes) {
         node.parent = this;
         children.addLast(node);
      }
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public Node getParent() {
      return parent;
   }
   
   public Node add(Node ... nodes) {
      for(Node node : nodes) {
         children.addLast(node);
      }
      return this;
   }
   
   public Node add(Node node) {
      children.addLast(node);
      return node;
   }
   
   public LinkedList<Node> getChildren() {
      return children;
   }
   
   public boolean hasChild() {
      return children != null ? children.size() > 0 : false;
   }
   
   public Node getSibling() {
      if(parent == null) return null;
      LinkedList<Node> child = parent.getChildren();
      if(child != null) {
         while(true) {
            Node node = child.peek();
            if(node == null) return null;
            else if(node == this) return child.peek();
         }
      }
      return null;
   }
   
   public static Node createMock(int level) {
      Node root = new Node("..", new Node[] {
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
         LinkedList<Node> child = root.children;
         for(Node node : child) {
            for(Node sel : node.children) {
               foo(sel, level - 2);
            }
         }
      }
      return root;
   }
   
   private static void foo(Node node, int deep) {
      if(deep == 0) return;
      Node added = node.add(new Node(node.name + ".1"));
      foo(added, --deep);
   }
}
