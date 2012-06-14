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

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.web.application.RequestContext;
import org.gatein.api.GateIn;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.commons.Range;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.SiteQuery;
import org.gatein.common.NotYetImplemented;
import org.gatein.portal.api.impl.portal.DataStorageContext;
import org.gatein.portal.api.impl.portal.SiteImpl;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.gatein.common.util.ParameterValidation.*;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class GateInImpl extends DataStorageContext implements GateIn, Startable, GateIn.LifecycleManager
{
   private static final Query<PortalConfig> SITES = new Query<PortalConfig>(SiteType.PORTAL.getName(), null, PortalConfig.class);
   private static final Query<PortalConfig> SPACES = new Query<PortalConfig>(SiteType.GROUP.getName(), null, PortalConfig.class);
   private static final Query<PortalConfig> DASHBOARDS = new Query<PortalConfig>(SiteType.USER.getName(), null, PortalConfig.class);

   private Map<PropertyType, Object> properties = new HashMap<PropertyType, Object>(7);
   private LifecycleManager lcManager = GateIn.NO_OP_MANAGER;

   private final NavigationService navigationService;
   public GateInImpl(DataStorage dataStorage, NavigationService navigationService)
   {
      super(dataStorage);
      this.navigationService = navigationService;
   }

   @Override
   public List<Site> getSites()
   {
      List<PortalConfig> list = query(SITES);
      list.addAll(query(SPACES));
      list.addAll(query(DASHBOARDS));

      return fromList(list);
   }

   @Override
   public List<Site> getSites(Site.Type siteType)
   {
      throwIllegalArgExceptionIfNull(siteType, "Site.Type");

      switch (siteType)
      {
         case SITE:
            return fromList(query(SITES));
         case SPACE:
            return fromList(query(SPACES));
         case DASHBOARD:
            return fromList(query(DASHBOARDS));
         default:
            throw new IllegalArgumentException(siteType + " is not recognized as a valid site type.");
      }
   }

   @Override
   public List<Site> getSites(Range range)
   {
      throwIllegalArgExceptionIfNull(range, "Range");

      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site getSite(Site.Id siteId)
   {
      throwIllegalArgExceptionIfNull(siteId, "Site.Id");

      return siteImpl(siteId).getSite();
   }

   @Override
   public Site getSite(Site.Type type, String name)
   {
      throwIllegalArgExceptionIfNull(type, "Site.Type");
      throwIllegalArgExceptionIfNull(name, "name");

      return getSite(Site.Id.create(type, name));
   }

   @Override
   public List<Site> getSites(Site.Type siteType, Range range)
   {
      throwIllegalArgExceptionIfNull(siteType, "Site.Type");
      throwIllegalArgExceptionIfNull(range, "range");

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
      throwIllegalArgExceptionIfNull(siteType, "Site.Type");
      throwIllegalArgExceptionIfNull(name, "name");

      return siteImpl(Site.Id.create(siteType, name)).addSite();
   }

   @Override
   public void removeSite(Site.Id siteId) throws EntityNotFoundException
   {
      throwIllegalArgExceptionIfNull(siteId, "Site.Id");

      siteImpl(siteId).removeSite();
   }

   @Override
   public void removeSite(Site.Type siteType, String siteName) throws EntityNotFoundException
   {
      throwIllegalArgExceptionIfNull(siteType, "Site.Type");
      throwIllegalArgExceptionIfNull(siteName, "Site name");

      removeSite(Site.Id.create(siteType, siteName));
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


   public void start()
   {
      // nothing to do
   }

   public void stop()
   {
      // nothing to do
   }

   public NavigationService getNavigationService()
   {
      return navigationService;
   }

   private List<Site> fromList(List<PortalConfig> internalSites)
   {
      List<Site> sites = new ArrayList<Site>(internalSites.size());
      for (PortalConfig internalSite : internalSites)
      {
         Site.Id siteId = SiteImpl.fromSiteKey(new SiteKey(internalSite.getType(), internalSite.getName()));
         sites.add(siteImpl(siteId));
      }
      return sites;
   }

   private SiteImpl siteImpl(Site.Id siteId)
   {
      return new SiteImpl(siteId, this);
   }

//   private PortalConfig getPortalConfig(Site.Id siteId)
//   {
//      try
//      {
//         begin();
//
//         SiteKey siteKey = SiteImpl.createMOPSiteKey(siteId);
//
//         return getDataStorage().getPortalConfig(siteKey.getTypeName(), siteKey.getName());
//      }
//      catch (Exception e)
//      {
//         throw new UndeclaredThrowableException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   private void removePortalDataFor(Site.Id siteId) throws EntityNotFoundException
//   {
//      PortalKey key = SiteImpl.createPortalKey(siteId);
//      try
//      {
//         begin();
//         ModelDataStorage mds = getDataStorage();
//         PortalData data = mds.getPortalConfig(key);
//         if (data == null)
//         {
//            throw new EntityNotFoundException("Site " + siteId + " does not exist.");
//         }
//         mds.remove(data);
//      }
//      catch (EntityNotFoundException e)
//      {
//         throw e;
//      }
//      catch (Exception e)
//      {
//         throw new UndeclaredThrowableException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }

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
}
