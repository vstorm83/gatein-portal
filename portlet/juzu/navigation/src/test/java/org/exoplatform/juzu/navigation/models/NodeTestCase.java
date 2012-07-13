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


import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class NodeTestCase extends TestCase
{

   public void testFindNodeById() {
      Node root = NodeUtil.createMock(0);
      Node home = NodeUtil.findNode(root, "root/home");
      assertNotNull(home);
      assertEquals("root/home", home.getId());
   }
   
   public void testPrevNode() {
      Node root = NodeUtil.createMock(0);
      Node group = NodeUtil.findNode(root, "root/group");
      assertNotNull(group);
      Node home = group.previous();
      assertNotNull(home);
      assertEquals("root/home", home.getId());
   }
   
   public void testNextNode() {
      Node root = NodeUtil.createMock(0);
      Node user = NodeUtil.findNode(root, "root/user");
      assertNotNull(user);
      Node empty2 = user.next();
      assertNotNull(empty2);
      assertEquals("root/empty2", empty2.getId());
      assertNull(empty2.next());
   }
   
   public void testMoveUp() {
      Node root = NodeUtil.createMock(0);
      Node empty2 = NodeUtil.findNode(root, "root/empty2");
      empty2.moveUp();
      Node user = empty2.next();
      assertNotNull(user);
      assertEquals("root/user", user.getId());
      assertNull(user.next());
   }
   
   public void testModeDown() {
      Node root = NodeUtil.createMock(0);
      Node user = NodeUtil.findNode(root, "root/user");
      user.moveDown();
      Node empty2 = user.previous();
      assertNotNull(empty2);
      assertEquals("root/empty2", empty2.getId());
      assertNull(user.next());
   }
   
   public void testDelete() {
      Node root = NodeUtil.createMock(0);
      Node node = NodeUtil.findNode(root, "root/home");
      assertTrue(node.delete());
      assertNull(NodeUtil.findNode(root, "root/home"));
   }
   
   public void testCopy() {
      Node root = NodeUtil.createMock(0);
      Node home = NodeUtil.findNode(root, "root/home");
      Node user = NodeUtil.findNode(root, "root/user");
      home.copy(user);
      assertNotNull(NodeUtil.findNode(user, "root/user/home"));
   }
   
   public void testCut() {
   }
}
