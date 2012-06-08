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

package org.gatein.less.model;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */

public class Module
{
   private String input;
   
   private String output;
   
   private boolean hasExternalImport = false;

   public String getInput()
   {
      return input;
   }

   public void setInput(String input)
   {
      this.input = input;
   }
   
   public String getInputDirectory()
   {
      return input.substring(0, input.lastIndexOf('/'));
   }

   public String getOutput()
   {
      return output;
   }

   public void setOutput(String output)
   {
      this.output = output;
   }
   
   public void setExternalImport(String s)
   {
      hasExternalImport = Boolean.valueOf(s);
   }
   
   public boolean hasExternalImport()
   {
      return hasExternalImport;
   }
}
