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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
public class ImportEntry
{
   private String name;
   
   private Set<String> locations;
   
   private Map<String, ImportEntry> dependencies;
   
   private byte[] data;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Set<String> getLocations()
   {
      if(locations == null) locations = new HashSet<String>();
      return Collections.unmodifiableSet(locations);
   }

   public void addLocation(String location)
   {
      if(this.locations == null) this.locations = new HashSet<String>();
      this.locations.add(location);
   }
   
   public void addLocations(Set<String> locations)
   {
      this.locations = locations;
   }
   
   public Map<String, ImportEntry> getDependencies()
   {
      if(dependencies == null) dependencies = new HashMap<String, ImportEntry>();
      return Collections.unmodifiableMap(dependencies);
   }
   
   public void addDenepency(ImportEntry importEntry)
   {
      if(dependencies == null) dependencies = new HashMap<String, ImportEntry>();
      dependencies.put(importEntry.getName(), importEntry);
   }

   public byte[] getData()
   {
      return data;
   }

   public void setData(byte[] data)
   {
      this.data = data;
   }
   
   @Override
   public String toString()
   {
      return "[" + name + "]";
   }
}
