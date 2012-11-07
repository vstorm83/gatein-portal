/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.gatein.portal.controller.resource;

import org.exoplatform.commons.utils.PropertyManager;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
class ScriptResult
{
   final long lastModified;

   private ScriptResult()
   {
      this(0);
   }
   
   private ScriptResult(long lastModified) 
   {
      this.lastModified = lastModified;
   }

   static class Resolved extends ScriptResult
   {

      /** . */
      final byte[] bytes;           

      Resolved(byte[] bytes, long lastModified)
      {
         super(lastModified);
         this.bytes = bytes;         
      }            
      
      boolean isModified(long ifModifiedSince) 
      {
         if (PropertyManager.isDevelopping()) 
         {
            return true;            
         }
         else
         {
            return lastModified > ifModifiedSince;
         }
      }
   }
   
   static class Error extends ScriptResult
   {

      /** . */
      final String message;

      Error(String message)
      {
         this.message = message;
      }
   }

   static ScriptResult NOT_FOUND = new ScriptResult();

}
