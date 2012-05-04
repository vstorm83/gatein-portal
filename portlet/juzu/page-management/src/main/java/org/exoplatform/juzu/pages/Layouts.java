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
package org.exoplatform.juzu.pages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class Layouts
{
   private static LinkedHashMap<String, Layout[]> source;
   
   static 
   {
      source = new LinkedHashMap<String, Layout[]>();
      source.put("Page Configs", new Layout[] { 
         new Layout("Empty Layout", "empty", "EmptyLayout"), 
         new Layout("Dashboard Layout", "dashboard", "DashboardLayout") 
      });
      
      source.put("Column Page Configs", new Layout[] {
         new Layout("Two Column Layout", "two-columns", "TwoColumnsLayout"),
         new Layout("Three Column Layout", "three-columns", "ThreeColumnsLayout")
      });
      
      source.put("Row Page Configs", new Layout[] {
         new Layout("Two Row Layout", "two-rows", "TwoRowsLayout"),
         new Layout("Three Row Layout", "three-rows", "ThreeRowsLayout")
      });
      
      source.put("Tabs Page Configs", new Layout[] {
         new Layout("Two Tabs", "two-tabs", "TwoTabsLayout"),
         new Layout("Three Tabs", "three-tabs", "ThreeTabsLayout")
      });
      
      source.put("Mix Page Configs", new Layout[] {
         new Layout("Two Columns One Row Layout", "two-columns-one-row", "TwoColumnsOneRowLayout"),
         new Layout("One Row Two Columns Layout", "one-row-two-columns", "OneRowTwoColumnsLayout"),
         new Layout("Two Columns Three Rows Layout", "three-rows-two-columns", "ThreeRowsTwoColumnsLayout")
      });
   }
   
   public static Map<String, Layout[]> getData()
   {
      return Collections.unmodifiableMap(source);
   }

   public static class Layout 
   {
      private String name;
      
      private String value;
      
      private String icon;
      
      public Layout(String name, String value, String icon)
      {
         this.name = name;
         this.value = value;
         this.icon = icon;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public String getValue()
      {
         return value;
      }

      public void setValue(String value)
      {
         this.value = value;
      }

      public String getIcon()
      {
         return icon;
      }

      public void setIcon(String icon)
      {
         this.icon = icon;
      }
   }
}
