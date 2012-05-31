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

package org.gatein.portal.api.impl;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.pc.ExoKernelIntegration;
import org.exoplatform.portal.pom.data.ModelDataStorage;
import org.exoplatform.portal.pom.data.PortalData;
import org.exoplatform.portal.pom.data.PortalKey;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.RequestContext;
import org.gatein.api.GateIn;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.commons.Range;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.SiteQuery;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.util.ParameterValidation;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.portal.api.impl.lifecycle.RequestLifecycleManager;
import org.gatein.portal.api.impl.portal.SiteImpl;
import org.picocontainer.Startable;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a> */
public class GateInImpl implements GateIn, Startable, GateIn.LifecycleManager
{
   private static final Query<PortalData> SITES = new Query<PortalData>(SiteType.PORTAL.getName(), null, PortalData.class);
   private static final Query<PortalData> SPACES = new Query<PortalData>(SiteType.GROUP.getName(), null, PortalData.class);
   private static final Query<PortalData> DASHBOARDS = new Query<PortalData>(SiteType.USER.getName(), null, PortalData.class);

   public static String SITE_PORTAL = "portal";

   public static String SITE_GROUP = "group";

   public static String SITE_USER = "user";



   ExoContainerContext context;
//   private ExoContainer container;
   private ModelDataStorage dataStorage;
//   private ApplicationRegistryService registryService;
//   private GadgetRegistryService gadgetService;
//   private SourceStorage sourceStorage;
   private UserPortalConfigService configService;
   private Map<PropertyType, Object> properties = new HashMap<PropertyType, Object>(7);
   private LifecycleManager lcManager = GateIn.NO_OP_MANAGER;
   //private PortletInvoker portletInvoker;

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
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site getSite(Site.Id siteId)
   {
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
      return getSite(Site.Id.create(type, name));
   }



   @Override
   public List<Site> getSites(Site.Type siteType, Range range)
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
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
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void removeSite(Site site)
   {
      //TODO:
      throw new NotYetImplemented();
   }


   public void start()
   {
      //dataStorage = (ModelDataStorage)container.getComponentInstanceOfType(ModelDataStorage.class);
//      registryService = (ApplicationRegistryService)container.getComponentInstanceOfType(ApplicationRegistryService.class);
//      gadgetService = (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
//      sourceStorage = (SourceStorage)container.getComponentInstanceOfType(SourceStorage.class);
      //configService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class);
      //portletInvoker = (PortletInvoker)container.getComponentInstanceOfType(PortletInvoker.class);
   }

   public void stop()
   {
      // nothing to do
   }

   public ModelDataStorage getDataStorage()
   {
      //return (ModelDataStorage)context.getContainer().getComponentInstanceOfType(ModelDataStorage.class);
      return dataStorage;
   }

   public NavigationService getNavigationService()
   {
//      UserPortalConfigService configService =
//         (UserPortalConfigService)context.getContainer().getComponentInstanceOfType(UserPortalConfigService.class);
//      return configService.getNavigationService();
      return configService.getNavigationService();
   }

//   public ApplicationRegistryService getRegistryService()
//   {
//      return registryService;
//   }
//
//   public SourceStorage getSourceStorage()
//   {
//      return sourceStorage;
//   }

//   public PortletInvoker getPortletInvoker()
//   {
//      return portletInvoker;
//   }

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
      return RequestContext.getCurrentInstance().getLocale();
   }

//   public GadgetRegistryService getGadgetService()
//   {
//      return gadgetService;
//   }

//   public OrganizationService getOrganizationService()
//   {
//      return configService.getOrganizationService();
//   }
//
//   public UserACL getUserACL()
//   {
//      return configService.getUserACL();
//   }
//
   public DescriptionService getDescriptionService()
   {
      return configService.getDescriptionService();
   }
}
