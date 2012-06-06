/*
* JBoss, a division of Red Hat
* Copyright 2012, Red Hat Middleware, LLC, and individual contributors as indicated
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

package org.gatein.portal.api.impl;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.pom.data.ModelDataStorage;
import org.exoplatform.portal.pom.data.PortalData;
import org.exoplatform.portal.pom.data.PortalKey;
import org.exoplatform.web.application.RequestContext;
import org.gatein.api.GateIn;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.commons.Range;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.SiteQuery;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.util.ParameterValidation;
import org.gatein.portal.api.impl.portal.SiteImpl;
import org.picocontainer.Startable;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class GateInImpl implements GateIn, Startable, GateIn.LifecycleManager
{
   private static final Query<PortalData> SITES = new Query<PortalData>(SiteType.PORTAL.getName(), null, PortalData.class);
   private static final Query<PortalData> SPACES = new Query<PortalData>(SiteType.GROUP.getName(), null, PortalData.class);
   private static final Query<PortalData> DASHBOARDS = new Query<PortalData>(SiteType.USER.getName(), null, PortalData.class);

   ExoContainerContext context;
   private ModelDataStorage dataStorage;
   private UserPortalConfigService configService;
   private Map<PropertyType, Object> properties = new HashMap<PropertyType, Object>(7);
   private LifecycleManager lcManager = GateIn.NO_OP_MANAGER;

   public GateInImpl(ExoContainerContext context, ModelDataStorage dataStorage, UserPortalConfigService configService)
   {
      this.context = context;
      this.dataStorage = dataStorage;
      this.configService = configService;
   }

   public <T> T getProperty(PropertyType<T> property)
   {
      if (property == null)
      {
         return null;
      }

      Class<T> propertyType = property.getValueType();
      Object o = properties.get(property);
      return propertyType.cast(o);
   }

   public <T> void setProperty(PropertyType<T> property, T value)
   {
      if (property != null)
      {
         if (GateIn.LIFECYCLE_MANAGER.equals(property))
         {
            lcManager = GateIn.LIFECYCLE_MANAGER.getValueType().cast(value);
         }
         properties.put(property, value);
      }
   }

   @Override
   public List<Site> getSites()
   {
      try
      {
         begin();
         List<PortalData> sites = getDataStorage().find(SITES).getAll();
         List<PortalData> spaces = getDataStorage().find(SPACES).getAll();
         List<PortalData> dashboards = getDataStorage().find(DASHBOARDS).getAll();

         List<Site> allSites = new LinkedList<Site>();

         // sites
         for (PortalData portalData : sites)
         {
            allSites.add(new SiteImpl(Site.Type.SITE, portalData.getName(), this));
         }

         // spaces
         for (PortalData portalData : spaces)
         {
            allSites.add(new SiteImpl(Site.Type.SPACE, portalData.getName(), this));
         }

         // dashboards
         for (PortalData portalData : dashboards)
         {
            allSites.add(new SiteImpl(Site.Type.DASHBOARD, portalData.getName(), this));
         }

         return allSites;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         end();
      }
   }

   @Override
   public List<Site> getSites(Site.Type siteType)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteType,  "Site.Type");

      try
      {
         List<PortalData> sites = null;

         switch (siteType)
         {
            case SITE:
               sites = getDataStorage().find(SITES).getAll();
               break;
            case SPACE:
               sites = getDataStorage().find(SPACES).getAll();
               break;
            case DASHBOARD:
               sites = getDataStorage().find(DASHBOARDS).getAll();
               break;
            default:
               throw new IllegalArgumentException("Not supported Site.Type: " + siteType);
         }

         List<Site> allSites = new LinkedList<Site>();

         // sites
         for (PortalData portalData : sites)
         {
            allSites.add(new SiteImpl(siteType, portalData.getName(), this));
         }

         return allSites;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         end();
      }
   }

   @Override
   public List<Site> getSites(Range range)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(range,  "Range");

      if (range == null)
      {
         throw new IllegalArgumentException("Range cannot be null");
      }

      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site getSite(Site.Id siteId)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteId,  "Site.Id");

      try
      {
         PortalKey key = SiteImpl.createPortalKey(siteId);

         PortalData data = getPortalDataFor(key);

         if (data != null)
         {
            return new SiteImpl(siteId, this);
         }

         return null;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         end();
      }
   }

   @Override
   public Site getSite(Site.Type type, String name)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(type,  "Site.Type");
      ParameterValidation.throwIllegalArgExceptionIfNull(name,  "name");

      return getSite(Site.Id.create(type, name));
   }

   @Override
   public List<Site> getSites(Site.Type siteType, Range range)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteType,  "Site.Type");
      ParameterValidation.throwIllegalArgExceptionIfNull(range,  "range");

      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site getDefaultSite()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public SiteQuery<Site> createSiteQuery()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site addSite(Site.Type siteType, String name)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteType,  "Site.Type");
      ParameterValidation.throwIllegalArgExceptionIfNull(name,  "name");

      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void removeSite(Site.Id siteId) throws EntityNotFoundException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteId, "Site.Id");
      removePortalDataFor(siteId);
   }

   @Override
   public void removeSite(Site.Type siteType, String siteName) throws EntityNotFoundException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(siteType, "Site.Type");
      ParameterValidation.throwIllegalArgExceptionIfNull(siteName, "Site name");

      removeSite(Site.Id.create(siteType, siteName));
   }


   public void start()
   {
      // nothing to do
   }

   public void stop()
   {
      // nothing to do
   }

   public ModelDataStorage getDataStorage()
   {
      return dataStorage;
   }

   public NavigationService getNavigationService()
   {
      return configService.getNavigationService();
   }

   private PortalData getPortalDataFor(PortalKey key)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(key, "Portal Id");
      try
      {
         begin();

         return getDataStorage().getPortalConfig(key);
      }
      catch (Exception e)
      {
         throw new UndeclaredThrowableException(e);
      }
      finally
      {
         end();
      }
   }

   private void removePortalDataFor(Site.Id siteId) throws EntityNotFoundException
   {
      PortalKey key = SiteImpl.createPortalKey(siteId);
      try
      {
         begin();
         ModelDataStorage mds = getDataStorage();
         PortalData data = mds.getPortalConfig(key);
         if (data == null)
         {
            throw new EntityNotFoundException("Site " + siteId + " does not exist.");
         }
         mds.remove(data);
      }
      catch (EntityNotFoundException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new UndeclaredThrowableException(e);
      }
      finally
      {
         end();
      }
   }

   public void begin()
   {
      lcManager.begin();
   }

   public void end()
   {
      lcManager.end();
   }

   public Locale getUserLocale()
   {
      //TODO: Workaround until RequestContext is sorted out in rest context
      RequestContext rc = RequestContext.getCurrentInstance();
      if (rc == null) return Locale.getDefault();

      return rc.getLocale();
   }

   public DescriptionService getDescriptionService()
   {
      return configService.getDescriptionService();
   }
}
