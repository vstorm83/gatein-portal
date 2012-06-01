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

import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.NavigationContext;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.mop.navigation.NavigationState;
import org.exoplatform.portal.mop.navigation.NodeModel;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.pom.data.PageData;
import org.exoplatform.portal.pom.data.PageKey;
import org.exoplatform.portal.pom.data.PortalKey;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.api.commons.PropertyType;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.util.ParameterValidation;
import org.gatein.portal.api.impl.GateInImpl;


import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class SiteImpl implements Site
{

   /** . */
   private static final Map<PageKey, PageImpl> EMPTY_CACHE = Collections.emptyMap();

   /** . */
   private static final Pattern PAGE_REF_PATTERN = Pattern.compile("^([^:]+)::([^:]+)::([^:]+)$");

   public static final Map<Type, String> OWNER_MAP;

   static
   {
      Map<Type, String> owners = new HashMap<Type, String>();
      owners.put(Type.SITE, "portal");
      owners.put(Type.SPACE, "group");
      owners.put(Type.DASHBOARD, "user");
      OWNER_MAP = Collections.unmodifiableMap(owners);
   }

   /** . */
   protected final Id id;
   protected final PageContainer pages;
   protected final GateInImpl gateIn;


   public SiteImpl(Id id, GateInImpl gateIn)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(id, "Site.Id");
      ParameterValidation.throwIllegalArgExceptionIfNull(gateIn, "GateInImpl");

      this.id = id;
      this.gateIn = gateIn;

      //
      pages = new PageContainer();

   }

   public SiteImpl(Type type, String name, GateInImpl gatein)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(type, "Site.Type");
      ParameterValidation.throwIllegalArgExceptionIfNull(name, "name");
      ParameterValidation.throwIllegalArgExceptionIfNull(gatein, "GateInImpl");

      this.id = Id.create(type, name);
      this.gateIn = gatein;

      //
      pages = new PageContainer();
   }

   @Override
   public Id getId()
   {
      return id;
   }

   @Override
   public String toString()
   {
      return getId().getType() + "::" + getId().getName() + "\n" + getNavigation().toString();
   }

   @Override
   public String getDisplayName()
   {
      //TODO
      return getId().getName();
   }

   @Override
   public void setDisplayName(String displayName)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(displayName,  "displayName");

      //TODO
      throw new NotYetImplemented();
   }

   @Override
   public String getDescription()
   {
      //TODO
      throw new NotYetImplemented();
   }

   @Override
   public void setDescription(String description)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(description,  "description");

      //TODO
      throw new NotYetImplemented();
   }

   public Navigation getNavigation()
   {
      GateInImpl gateIn = getGateInImpl();
      NavigationService service = gateIn.getNavigationService();

      try
      {
         gateIn.begin();
         NavigationContext navigation = service.loadNavigation(getMOPSiteKey());

         if (navigation != null)
         {
            NodeModel<NavigationImpl> nodeModel = new NavigationImpl.NavigationNodeModel(this, gateIn);

            return service.loadNode(nodeModel, navigation, Scope.CHILDREN, null).getNode();
         }
         else
         {
            return null;
         }
      }
      finally
      {
         gateIn.end();
      }
   }

   public int getPriority()
   {
      GateInImpl gateIn = getGateInImpl();
      NavigationService service = gateIn.getNavigationService();

      try
      {
         gateIn.begin();
         NavigationContext navigation = service.loadNavigation(getMOPSiteKey());

         NavigationState state = navigation.getState();
         return state != null ? state.getPriority() : 1;
      }
      finally
      {
         gateIn.end();
      }
   }

   @Override
   public <T> T getProperty(PropertyType<T> property)
   {

      if (property == null)
      {
         return null;
      }

      //TODO
      throw new NotYetImplemented();
   }

   @Override
   public <T> void setProperty(PropertyType<T> property, T value)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(property, "property");


      //TODO
      throw new NotYetImplemented();
   }

   @Override
   public void setPriority(int priority)
   {
      //TODO
      throw new NotYetImplemented();
   }

   @Override
   public Page getPage(String pageName)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(pageName, "pageName");

      return pages.getPageByName(pageName);
   }

   @Override
   public Navigation getNavigation(String navigationId)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(navigationId, "navigationId");

      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Navigation getNavigation(String... path)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(path, "path");

      //TODO:
      throw new NotYetImplemented();
   }

   public SiteKey getMOPSiteKey()
   {
      switch (getId().getType())
      {
         case SITE:
            return SiteKey.portal(getId().getName());
         case SPACE:
            return SiteKey.group(getId().getName());
         case DASHBOARD:
            return SiteKey.user(getId().getName());
      }

      throw new RuntimeException("Not recognized type: " + getId().getType());
   }

   public static PortalKey createPortalKey(Site.Id id)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(id, "id");

      return new PortalKey(OWNER_MAP.get(id.getType()), id.getName());
   }

   public PortalKey getPortalKey()
   {
      return createPortalKey(this.getId());
   }

   GateInImpl getGateInImpl()
   {
      return gateIn;
   }



   class PageContainer
   {

      /** . */
      private final Query<PageData> pageDataQuery;

      /** A local cache so we return the same pages. */
      private Map<PageKey, PageImpl> cache;

      private PageContainer()
      {
         this.pageDataQuery = new Query<PageData>(OWNER_MAP.get(getId().getType()), null, PageData.class);
         this.cache = EMPTY_CACHE;
      }

      PageImpl getPageByRef(String pageRef)
      {
         Matcher m = PAGE_REF_PATTERN.matcher(pageRef);
         m.matches();
         PageKey key = new PageKey(m.group(1), m.group(2), m.group(3));
         return getPageData(key);
      }

      PageImpl getPageByName(String name)
      {
         SiteKey siteKey = getMOPSiteKey();
         PageKey key = new PageKey(siteKey.getTypeName(), siteKey.getName(), name);
         return getPageData(key);
      }

      private PageImpl getPageData(PageKey key)
      {
         PageImpl page = cache.get(key);
         if (page == null)
         {
            try
            {
               gateIn.begin();
               PageData data = gateIn.getDataStorage().getPage(key);
               if (data != null)
               {
                  page = new PageImpl(data, getId(), gateIn);
                  if (cache == EMPTY_CACHE)
                  {
                     cache = new HashMap<PageKey, PageImpl>();
                  }
                  cache.put(key, page);
               }
            }
            catch (Exception e)
            {
               throw new UndeclaredThrowableException(e);
            }
            finally
            {
               gateIn.end();
            }
         }
         return page;
      }
   }

}
