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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class Node
{
   private String name;
   
   private String id;
   
   private Node parent;
   
   private LinkedList<Node> children = new LinkedList<Node>();
   
   private Map<String, String> attributes = new HashMap<String,String>();

   public Node(String name) {
      this.name = name;
      this.id = getId();
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
   
   public String getId() {
      StringBuilder sb = new StringBuilder();
      sb.append(parent != null ? parent.getId() : "");
      sb.append("root".equals(name) ? "" : "/").append(name);
      id = sb.toString();
      return id;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public Node getParent() {
      return parent;
   }
   
   public Iterator<Map.Entry<String, String>> getAttributes() {
      return attributes.entrySet().iterator();
   }
   
   public void setAttributes(String[] attributes) {
      for(String attr : attributes){
         int index = attr.indexOf('=');
         if(index > 0) {
            this.attributes.put(attr.substring(0, index), attr.substring(index + 1));
         } else {
            this.attributes.put(attr, "");
         }
      }
   }
   
   public void setAttribute(String key, String value) {
      attributes.put(key, value);
   }
   
   public String getAttribute(String key) {
      return attributes.get(key);
   }
   
   public Node add(Node ... nodes) {
      for(Node node : nodes) {
         node.parent = this;
         children.addLast(node);
      }
      return this;
   }
   
   public Node add(Node node) {
      node.parent = this;
      children.addLast(node);
      return node;
   }
   
   public List<Node> getChildren() {
      return Collections.unmodifiableList(children);
   }
   
   public void setChildren(LinkedList<Node> children) {
      this.children = children;
   }
   
   public boolean hasChild() {
      return children != null ? children.size() > 0 : false;
   }
   
   public Node previous() {
      if(parent == null) return null;
      List<Node> children = parent.getChildren();
      for(int i = 0; i < children.size(); i++) {
         Node sel = children.get(i);
         if(sel.equals(this)) return i == 0 ? null : children.get(i - 1);
      }
      return null;
   }
   
   public Node next() {
      if(parent == null) return null;
      List<Node> children = parent.getChildren();
      for(int i = 0; i < children.size(); i++) {
         Node sel = children.get(i);
         if(sel == this) {
            return i == children.size() - 1 ? null : children.get(i + 1);
         }
      }
      return null;
   }
   
   public boolean delete() {
      return parent.children.remove(this);
   }
   
   public void moveUp() {
      Node prevNode = previous();
      if(prevNode == null) return;
      
      List<Node> children = parent.getChildren();
      LinkedList<Node> holder = new LinkedList<Node>();
      for(Node sel : children) {
         if(sel.equals(prevNode)) {
            holder.addLast(this);
            continue;
         } else if(sel.equals(this)) {
            holder.addLast(prevNode);
            continue;
         }
         holder.addLast(sel);
      }
      parent.setChildren(holder);
   }
   
   public void moveDown() {
      Node nextNode = next();
      if(nextNode == null) return;
      
      List<Node> children = parent.getChildren();
      LinkedList<Node> holder = new LinkedList<Node>();
      for(Node sel : children) {
         if(sel.equals(this)) {
            holder.addLast(nextNode);
            continue;
         } else if(sel.equals(nextNode)) {
            holder.addLast(this);
            continue;
         }
         holder.addLast(sel);
      }
      parent.setChildren(holder);
   }
   
   @Override
   public Node clone() {
      Node node = new Node(this.name, this.parent, this.children);
      return node;
   }
   
   @Override
   public String toString() {
      return getId();
   }
   
   public Node clone(Node dest) {
      Node node = this.clone();
      dest.add(node);
      return dest;
   }
   
   public Node copy(Node dest) {
      return clone(dest);
   }
   
   public Node cut(Node dest) {
      if(this.delete()) {
         dest.add(this);
      }
      return dest;
   }
   
   @Override
   public boolean equals(Object obj) {
      Node that = (Node)obj;
      return this.getId().equals(that.getId());
   }
}
