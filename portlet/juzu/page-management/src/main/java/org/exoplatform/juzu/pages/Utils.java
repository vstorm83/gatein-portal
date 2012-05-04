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
import java.util.Map;

import org.juzu.Response;
import org.juzu.io.CharStream;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class Utils
{
   
   public static Response.Resource<CharStream> createJSON(final Map<String, String> data)
   {
      Response.Resource<CharStream> json = new Response.Resource<CharStream>()
      {

         @Override
         public String getMimeType()
         {
            return "application/json";
         }

         @Override
         public int getStatus()
         {
            return 200;
         }

         @Override
         public Class<CharStream> getKind()
         {
            return CharStream.class;
         }

         @Override
         public void send(CharStream stream) throws IOException
         {
            stream.append("{");
            for (Map.Entry<String, String> entry : data.entrySet())
            {
               stream.append("\"" + entry.getKey() + "\"");
               stream.append(":");
               stream.append("\"" + entry.getValue() + "\"");
               stream.append(",");
            }
            stream.append("}");
         }
      };
      return json;
   }
}
