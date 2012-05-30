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
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.pc.ExoKernelIntegration;
import org.exoplatform.portal.pom.data.ModelDataStorage;
import org.exoplatform.portal.pom.data.PageData;
import org.exoplatform.portal.pom.data.PageKey;
import org.exoplatform.portal.pom.data.PortalData;
import org.exoplatform.portal.pom.data.PortalKey;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.web.application.RequestContext;
import org.gatein.api.GateIn;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.commons.Range;
import org.gatein.api.portal.Dashboard;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.PortalObject;
import org.gatein.api.portal.PortalObjectQuery;
import org.gatein.api.portal.Site;
import org.gatein.api.portal.Space;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.util.ParameterValidation;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.portal.api.impl.portal.DashboardImpl;
import org.gatein.portal.api.impl.portal.SiteImpl;
import org.gatein.portal.api.impl.portal.SpaceImpl;
import org.gatein.portal.api.impl.portal.PageImpl;
import org.picocontainer.Startable;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a> */
public class GateInImpl implements GateIn, Startable, LifecycleManager
{
   private static final Query<PortalData> PORTALS = new Query<PortalData>(SiteType.PORTAL.getName(), null, PortalData.class);

   public static String SITE_PORTAL = "portal";

   public static String SITE_GROUP = "group";

   public static String SITE_USER = "user";

//   private static final String GROUP_CHARS = "\\w|-|_";

//   public static final String SITE_TYPE_COMPONENT = "type";
//   public static final String SITE_NAME_COMPONENT = "name";
//   public static final String APPLICATION_COMPONENT = "application";
//   public static final String PORTLET_COMPONENT = "portlet";
//   public static final String INVOKER_COMPONENT = "invoker";
//   public static final String CATEGORY_COMPONENT = "category";

//   public static final Pattern INVOKER_COMPONENT_PATTERN = Pattern.compile("\\w+");



   private ExoContainer container;
   private ModelDataStorage dataStorage;
//   private ApplicationRegistryService registryService;
//   private GadgetRegistryService gadgetService;
//   private SourceStorage sourceStorage;
   private UserPortalConfigService configService;
   private Map<PropertyType, Object> properties = new HashMap<PropertyType, Object>(7);
   private LifecycleManager lcManager = GateIn.NO_OP_MANAGER;
   private PortletInvoker portletInvoker;

   public GateInImpl(ExoContainerContext context, InitParams params, ConfigurationManager configurationManager, ExoKernelIntegration exoKernelIntegration)
   {
      container = context.getContainer();
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
         List<PortalData> portals = dataStorage.find(PORTALS).getAll();

         List<Site> sites = new LinkedList<Site>();

         for (PortalData portalData : portals)
         {
            PortalKey key = portalData.getKey();
            sites.add(new SiteImpl(portalData.getName(), this));
         }

         return sites;

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
   public Site getSite(String siteId)
   {
      PortalKey key = new PortalKey(SITE_PORTAL, siteId);

//      Type<Site> siteType = siteId.getType();
      PortalData data = getPortalDataFor(key);

      if (key.getType().equalsIgnoreCase(SITE_PORTAL))
      {
         return new SiteImpl(data.getKey().getId(), this);
      }
      else
      {
         throw new IllegalArgumentException("Provided id doesn't contain proper type related to site: " + key.getType());
      }
   }

   @Override
   public Site getDefaultSite()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public PortalObjectQuery<Site> createSiteQuery()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Site addSite(String name)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public List<Space> getSpaces()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public List<Space> getSpaces(Range range)
   {
      try
      {
         begin();

         //TODO: must be better way
         final GroupHandler groupHandler = getOrganizationService().getGroupHandler();
         Collection<Group> groups = groupHandler.getAllGroups();

         List<Space> spaces = new LinkedList<Space>();


         for (Group group : groups)
         {
            spaces.add(getSpace(group.getId()));
         }

         return spaces;
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
   public Space getSpace(String spaceId)
   {
      PortalKey key = new PortalKey(SITE_GROUP, spaceId);

//      Type<Site> siteType = siteId.getType();
      PortalData data = getPortalDataFor(key);

      if (key.getType().equalsIgnoreCase(SITE_GROUP))
      {
         return new SpaceImpl(data.getKey().getId(), this);
      }
      else
      {
         throw new IllegalArgumentException("Provided id doesn't contain proper type related to space: " + key.getType());
      }
   }

   @Override
   public Space getSpace(String... groupId)
   {
      StringBuilder groupIdBuilder = new StringBuilder();
      for (String partId : groupId)
      {
         groupIdBuilder.append("/")
            .append(partId);

      }

      return getSpace(groupIdBuilder.toString());

   }

   @Override
   public PortalObjectQuery<Space> createSpaceQuery()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Space addSpace(String name, String groupId)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public List<Dashboard> getDashboards()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public List<Dashboard> getDashboards(Range range)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Dashboard getDashboard(String dashboardId)
   {
      PortalKey key = new PortalKey(SITE_USER, dashboardId);

//      Type<Site> siteType = siteId.getType();
      PortalData data = getPortalDataFor(key);

      if (key.getType().equalsIgnoreCase(SITE_USER))
      {
         return new DashboardImpl(data.getKey().getId(), this);
      }
      else
      {
         throw new IllegalArgumentException("Provided id doesn't contain proper type related to dashboard: " + key.getType());
      }
   }


   @Override
   public PortalObjectQuery<Dashboard> createDashboardQuery()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Navigation getNavigation(String navigationId)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Navigation getNavigation(String... path)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   //   public List<Site> getPortals()
//   {
//      try
//      {
//         begin();
//         final List<PortalData> portals = dataStorage.find(PORTALS).getAll();
//
//         return new AdaptedIterableIdentifiableCollection<PortalData, Site>(portals.size(), portals.iterator())
//         {
//            public Portal adapt(PortalData old)
//            {
//               return new SiteImpl(old, GateInImpl.this);
//            }
//
//            public boolean contains(Id<Site> id)
//            {
//               return getPortalDataFor((Site.Id)id) != null;
//            }
//         };
//      }
//      catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   public Portal getDefaultPortal()
//   {
//      return getPortal(Site.Id.createPortal("classic")); // todo: check
//   }
//
//   public IterableIdentifiableCollection<Site> getSites()
//   {
//      IterableIdentifiableCollection<Site> groupSites = getGroupSites();
//      IterableIdentifiableCollection<Site> portals = getPortals();
//      IterableIdentifiableCollection<Site> dashboards = getDashboards();
//
//      AggregatedIterableIdentifiableCollection<Site> collection = new AggregatedIterableIdentifiableCollection<Site>();
//      collection.addCollection(groupSites);
//      collection.addCollection(portals);
//      collection.addCollection(dashboards);
//
//      return collection;
//   }
//
//   private IterableIdentifiableCollection<Site> getDashboards()
//   {
//      try
//      {
//         begin();
//         final UserHandler userHandler = getOrganizationService().getUserHandler();
//
//         // todo: optimize
//         List<User> users = userHandler.getUserPageList(1000).getAll();
//
//         // todo: check for correctness
//         return new AdaptedIterableIdentifiableCollection<User, Site>(users.size(), users.iterator())
//         {
//
//            public Site adapt(User old)
//            {
//               return getDashboard(old.getUserName());
//            }
//
//            public boolean contains(Id<Site> t)
//            {
//               try
//               {
//                  return dataStorage.loadDashboard(t.toString()) != null;
//               }
//               catch (Exception e)
//               {
//                  return false;
//               }
//            }
//         };
//      }
//      catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   public IterableIdentifiableCollection<Site> getGroupSites()
//   {
//      try
//      {
//         begin();
//
//         final GroupHandler groupHandler = getOrganizationService().getGroupHandler();
//         Collection groups = groupHandler.getAllGroups();
//
//         return new AdaptedIterableIdentifiableCollection<Object, Site>(groups.size(), groups.iterator())
//         {
//            public boolean contains(Id<Site> siteId)
//            {
//               try
//               {
//                  return groupHandler.findGroupById(siteId.toString()) != null;
//               }
//               catch (Exception e)
//               {
//                  return false;
//               }
//            }
//
//            public Site adapt(Object old)
//            {
//               Group group = (Group)old;
//               // Should parse group id
//               return getGroupSite(group.getId());
//            }
//         };
//      }
//      catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   public IterableIdentifiableCollection<Site> getGroupSites(String userId)
//   {
//      final GroupHandler groupHandler = getOrganizationService().getGroupHandler();
//      try
//      {
//         begin();
//         final String id = userId.toString();
//         Collection groups = groupHandler.findGroupsOfUser(id);
//
//         return new AdaptedIterableIdentifiableCollection<Object, Site>(groups.size(), groups.iterator())
//         {
//            public boolean contains(Id<Site> siteId)
//            {
//               try
//               {
//                  Group group = groupHandler.findGroupById(siteId.toString());
//                  return group != null && !groupHandler.findGroupByMembership(id, null).isEmpty();
//               }
//               catch (Exception e)
//               {
//                  return false;
//               }
//            }
//
//            public Site adapt(Object old)
//            {
//               Group group = (Group)old;
//               return getGroupSite(group.getId());
//            }
//         };
//      }
//      catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   public IterableIdentifiableCollection<Site> getPortalSites(String userId)
//   {
//      try
//      {
//         begin();
//         final List<PortalData> portalDatas = dataStorage.find(PORTALS).getAll();
//
//         // first build Identity based on user id so that we can check its permissions using UserACL... ugh! :(
//         final Collection membershipsByUser = getOrganizationService().getMembershipHandler().findMembershipsByUser(userId);
//         Collection<MembershipEntry> membershipEntries = new ArrayList<MembershipEntry>(membershipsByUser.size());
//         for (Object o : membershipsByUser)
//         {
//            Membership membership = (Membership)o;
//            membershipEntries.add(new MembershipEntry(membership.getGroupId(), membership.getMembershipType()));
//         }
//         final Identity identity = new Identity(userId, membershipEntries);
//
//         final List<Site> portals = new ArrayList<Site>(portalDatas.size());
//         final Filter<PortalData> filter = new Filter<PortalData>()
//         {
//            @Override
//            public boolean accept(PortalData item)
//            {
//               return getUserACL().hasPermission(identity, new PortalConfig(item));
//            }
//         };
//
//         for (PortalData portalData : portalDatas)
//         {
//            if (filter.accept(portalData))
//            {
//               portals.add(new SiteImpl(portalData, this));
//            }
//         }
//
//         return new AdaptedIterableIdentifiableCollection<Site, Site>(portals.size(), portals.iterator())
//         {
//            public Site adapt(Site old)
//            {
//               return old;
//            }
//
//            public boolean contains(Id<Site> id)
//            {
//               final PortalData portalData = getPortalDataFor((Site.Id)id);
//               return portalData != null && filter.accept(portalData);
//            }
//         };
//      }
//      catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
//      finally
//      {
//         end();
//      }
//   }
//
//   public <T extends Identifiable<T>> T get(Id<T> id)
//   {
//      Class<T> type = id.getIdentifiableType();
//
//      Object result = null;
//
//      if (Portal.class.equals(type))
//      {
//         result = getPortal((Site.Id)(Id)id);
//      }
//      else if (Page.class.equals(type))
//      {
//         try
//         {
//            begin();
//            PageData pageData = dataStorage.getPage(PageKey.create(id.toString()));
//            Page.Id pageId = (Page.Id)(Id)id;
//            result = new PageImpl(pageData, pageId.getSite(), this);
//         }
//         catch (Exception e)
//         {
//            throw new RuntimeException(e);
//         }
//         finally
//         {
//            end();
//         }
//      }
//      else if (Site.class.equals(type))
//      {
//         result = getSite((Site.Id)(Id)id);
//      }
//      else if (Content.class.isAssignableFrom(type))
//      {
//         // todo: split by types and optimize by calling portlet invoker or gadget registry directly
//         final IterableIdentifiableCollection<Site> portals = getPortals();
//         for (Site portal : portals)
//         {
//            result = ((Portal)portal).getContentRegistry().getContent((Content.Id)(Id)id);
//            if (result != null)
//            {
//               break;
//            }
//         }
//      }
//      else if (Category.class.equals(type))
//      {
//         // todo: optimize by adding portal id to category id (so that appropriate content registry can be retrieved) and calling application registry directly
//         final IterableIdentifiableCollection<Site> portals = getPortals();
//         for (Site portal : portals)
//         {
//            result = ((Portal)portal).getContentRegistry().getCategory(id.toString());
//            if (result != null)
//            {
//               break;
//            }
//         }
//      }
//      else if (ManagedContent.class.equals(type))
//      {
////         final String categoryId = id.getComponent(CATEGORY_COMPONENT);
////         final Category category = get(categoryId(categoryId));
////         result = category.getManagedContent(id.getComponent("name"));
//         throw new UnsupportedOperationException("disabled");
//      }
//      else if (Navigation.class.equals(type))
//      {
//         throw new UnsupportedOperationException("Id<" + type.getSimpleName() + "> not yet supported");
//      }
//      else
//      {
//         throw new UnsupportedOperationException("Id<" + type.getSimpleName() + "> not yet supported");
//      }
//
//      return type.cast(result);
//   }
//
//   public Portal getPortal(Site.Id portalId)
//   {
//      return (Portal)getSite(portalId);
//   }
//
//   public Site getGroupSite(String... names)
//   {
//      StringBuilder sb = new StringBuilder();
//      for (String name : names)
//      {
//         sb.append("/").append(name);
//      }
//      String groupId = sb.toString();
//      Site.Id siteId = Site.Id.createGroup(groupId);
//      return getSite(siteId);
//   }
//
//   public Site getDashboard(String userId)
//   {
//      Site.Id siteId = Site.Id.createDashboard(userId);
//      return getSite(siteId);
//   }
//

//
//   public Portlet.Id portletId(String application, String portlet)
//   {
//      throw new UnsupportedOperationException();
//   }




/*
   public <T extends Site> Id<T> siteId(Type<T> siteType, String siteName)
   {
      return SITE_CONTEXT.create(siteType.getValueType(), siteType.getName(), siteName);
   }
*/

//   public Page.Id pageId(Site.Id ownerSite, String pageName)
//   {
//      ParameterValidation.throwIllegalArgExceptionIfNull(ownerSite, "Owner Site Id");
//      return new Page.Id(ownerSite, pageName);
//   }

   public void start()
   {
      dataStorage = (ModelDataStorage)container.getComponentInstanceOfType(ModelDataStorage.class);
//      registryService = (ApplicationRegistryService)container.getComponentInstanceOfType(ApplicationRegistryService.class);
//      gadgetService = (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
//      sourceStorage = (SourceStorage)container.getComponentInstanceOfType(SourceStorage.class);
      configService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class);
      portletInvoker = (PortletInvoker)container.getComponentInstanceOfType(PortletInvoker.class);
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

//   public ApplicationRegistryService getRegistryService()
//   {
//      return registryService;
//   }
//
//   public SourceStorage getSourceStorage()
//   {
//      return sourceStorage;
//   }

   public PortletInvoker getPortletInvoker()
   {
      return portletInvoker;
   }

   private PortalData getPortalDataFor(PortalKey key)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(key, "Portal Id");
      try
      {
         begin();

         return dataStorage.getPortalConfig(key);
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

   public OrganizationService getOrganizationService()
   {
      return configService.getOrganizationService();
   }

   public UserACL getUserACL()
   {
      return configService.getUserACL();
   }

   public DescriptionService getDescriptionService()
   {
      return configService.getDescriptionService();
   }
}
