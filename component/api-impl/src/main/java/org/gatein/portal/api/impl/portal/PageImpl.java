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

import org.exoplatform.portal.pom.data.ModelDataStorage;
import org.exoplatform.portal.pom.data.PageData;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.PageType;
import org.gatein.api.portal.PortalObject;
import org.gatein.api.portal.PortalObjectType;
import org.gatein.common.NotYetImplemented;
import org.gatein.portal.api.impl.GateInImpl;

import java.util.List;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class PageImpl implements Page
{
   private final String portalObjectId;
   private final PortalObjectType portalObjectType;
   private PageData pageData;
   protected final GateInImpl gateIn;

   public PageImpl(PageData pageData, String portalObjectId, PortalObjectType type, GateInImpl gateIn)
   {
      this.portalObjectId = portalObjectId;
      this.portalObjectType = type;
      this.pageData = pageData;
      this.gateIn = gateIn;
   }

   public PortalObject getPortalObject()
   {

      switch (portalObjectType)
      {
         case SITE:
            return getGateInImpl().getSite(portalObjectId);

         case SPACE:
            return getGateInImpl().getSpace(portalObjectId);

         case DASHBOARD:
            return getGateInImpl().getDashboard(portalObjectId);
      }

      throw new RuntimeException("Not supported object type: " + portalObjectType);
   }

   public String getTitle()
   {
      return pageData.getTitle();
   }

   @Override
   public String getId()
   {
      return pageData.getId();
   }

   @Override
   public PageType getType()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void setType(PageType name)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public String getName()
   {
      return pageData.getName();
   }

   @Override
   public List<Navigation> getNavigations()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public <T> T getProperty(PropertyType<T> property)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public <T> void setProperty(PropertyType<T> property, T value)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public String toString()
   {
      return "'" + getName() + "' Page titled '" + getTitle() + "' id " + getId();
   }

   public void setTitle(String title)
   {
      try
      {
         getGateInImpl().begin();
         final ModelDataStorage dataStorage = getGateInImpl().getDataStorage();

         // recreate page with the new title
         final PageData newPageData = new PageData(pageData.getStorageId(), pageData.getId(), pageData.getName(), pageData.getIcon(),
            pageData.getTemplate(), pageData.getFactoryId(), title, pageData.getDescription(), pageData.getWidth(), pageData.getHeight(),
            pageData.getAccessPermissions(), pageData.getChildren(), pageData.getOwnerType(), pageData.getOwnerId(),
            pageData.getEditPermission(), pageData.isShowMaxWindow());

         // save new page
         dataStorage.save(newPageData);

         // remove previous data
         dataStorage.remove(pageData);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         getGateInImpl().end();
      }


   }

   GateInImpl getGateInImpl()
   {
      return gateIn;
   }


}
