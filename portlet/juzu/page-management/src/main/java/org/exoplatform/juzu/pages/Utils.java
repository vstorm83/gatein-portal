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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import juzu.Response;
import juzu.io.Stream;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class Utils
{
   
   public static Response.Content<Stream.Char> createJSON(final Map<String, String> data)
   {
      Response.Content<Stream.Char> json = new Response.Content<Stream.Char>(200, Stream.Char.class)
      {

         @Override
         public String getMimeType()
         {
            return "application/json";
         }

         @Override
         public Integer getStatus()
         {
            return 200;
         }

         @Override
         public void send(Stream.Char stream) throws IOException
         {
            stream.append("{");
            Iterator<Map.Entry<String, String>> i = data.entrySet().iterator();
            while(i.hasNext())
            {
               Map.Entry<String, String> entry = i.next();
               stream.append("\"" + entry.getKey() + "\"");
               stream.append(":");
               stream.append("\"" + entry.getValue() + "\"");
               if(i.hasNext())
               {
                  stream.append(",");
               }
            }
            stream.append("}");
         }
      };
      return json;
   }
}
