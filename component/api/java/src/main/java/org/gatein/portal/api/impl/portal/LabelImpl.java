/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.portal.api.impl.portal;

import org.gatein.api.portal.Label;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class LabelImpl implements Label
{
   //TODO: Possibly connect this to some context abstractly, since it's used for Site and Node

   private static final Value<String> NULL_VALUE = new LocalizedString(null, null);

   private Map<Locale, Value<String>> values;

   public LabelImpl(String value)
   {
      values = new HashMap<Locale, Value<String>>();
      Value<String> lv = (value == null) ? NULL_VALUE : new LocalizedString(null, value);
      values.put(null, lv);
   }

   @Override
   public String getValue()
   {
      return getValue(null).getValue();
   }

   @Override
   public void setValue(String value)
   {
      getValue(null).setValue(value);
   }

   @Override
   public boolean isLocalized()
   {
      return values.size() == 1 && values.keySet().iterator().next() == null;
   }

   @Override
   public Value<String> getValue(final Locale locale)
   {
      Value<String> value = values.get(locale);

      return (value == null) ? NULL_VALUE : value;
   }

   @Override
   public Value<String> setValue(Locale locale, String value)
   {
      Value<String> string = new LocalizedString(locale, value);
      values.put(locale, string);
      return string;
   }

   @Override
   public void removeValue(Locale locale)
   {
      values.remove(locale);
   }

   @Override
   public Iterator<Value<String>> iterator()
   {
      return values.values().iterator();
   }

   private static class LocalizedString implements Value<String>
   {
      private String value;
      private Locale locale;

      private LocalizedString(Locale locale, String value)
      {
         this.locale = locale;
         this.value = value;
      }

      @Override
      public String getValue()
      {
         return value;
      }

      @Override
      public void setValue(String value)
      {
         this.value = value;
      }

      @Override
      public Locale getLocale()
      {
         return locale;
      }
   }
}
