/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

import org.exoplatform.portal.mop.SiteKey;
import org.gatein.api.portal.PortalObjectType;
import org.gatein.api.portal.Site;
import org.gatein.portal.api.impl.GateInImpl;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class SiteImpl extends PortalObjectImpl implements Site
{
   public static String OWNER_TYPE = "portal";

   public SiteImpl(String siteId, GateInImpl gateIn)
   {
      super(siteId, gateIn);
   }

   @Override
   public String getName()
   {
      return getId();
   }

   @Override
   String getOwnerType()
   {
      return OWNER_TYPE;
   }

   @Override
   public PortalObjectType getType()
   {
      return PortalObjectType.SITE;
   }

   protected SiteKey getMOPSiteKey()
   {
      return SiteKey.portal(getName());
   }
}
