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
package org.exoplatform.juzu.pages.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.exoplatform.juzu.pages.Layouts;
import org.exoplatform.juzu.pages.Layouts.Layout;
import org.exoplatform.juzu.pages.Utils;
import juzu.Resource;
import juzu.Response;
import juzu.plugin.ajax.Ajax;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class PageLayout
{
   
   @Ajax
   @Resource
   public Response changeLayoutCategory(String category) 
   {
      Layout firstLayout = Layouts.getData().get(category)[0];
      Map<String, String> map = new HashMap<String,String>();
      map.put("LayoutPreview", renderLayoutPreview(firstLayout.getName(), firstLayout.getValue(), firstLayout.getIcon()));
      map.put("LayoutItems", renderLayouts(category));
      return Utils.createJSON(map);
   }
   
   private String renderLayoutCategories() 
   {
      StringBuilder b = new StringBuilder();
      Set<String> categories = Layouts.getData().keySet();
      for(String cateogry : categories) 
      {
         b.append("<option value='").append(cateogry).append("'>").append(cateogry).append("</option>");
      }
      return b.toString();
   }
   
   private String renderLayouts(String category) 
   {
      Layout[] layouts = Layouts.getData().get(category);
      StringBuilder b = new StringBuilder();
      for(int i = 0; i < layouts.length; i++)
      {
         b.append("<li").append(i == 0 ? " class='active'>" : ">")
            //.append("<a").append(" href='").append("#!/").append(layouts[i].getValue()).append("/").append(layouts[i].getIcon()).append("'>")
            .append("<a").append(" value='").append(layouts[i].getValue()).append("' icon='").append(layouts[i].getIcon()).append("'>")
            .append(layouts[i].getName())
            .append("</a>")
         .append("</li>");
      }
      return b.toString();
   }
   
   private String renderLayoutPreview(String name, String value, String icon) 
   {
      StringBuilder b = new StringBuilder();
      b.append("<div style='text-align: center; margin-top: 20px;'>").append(name).append("</div>");
      b.append("<div class='").append(icon).append("' style='margin: auto;'></div>");
      b.append("<input type='hidden' name='layout' value='").append(value).append("' />");
      return b.toString();
   }
}
