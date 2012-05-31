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

import org.exoplatform.commons.utils.ExpressionUtil;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.mop.Described;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.NodeModel;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.pom.data.PortalKey;
import org.exoplatform.services.resources.ResourceBundleManager;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.gatein.api.GateIn;
import org.gatein.api.commons.PropertyType;
import org.gatein.api.commons.Range;
import org.gatein.api.portal.Navigation;
import org.gatein.api.portal.Page;
import org.gatein.api.portal.Site;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.text.EntityEncoder;
import org.gatein.common.util.EmptyResourceBundle;
import org.gatein.portal.api.impl.GateInImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@redhat.com">Boleslaw Dawidowicz</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 */
public class NavigationImpl implements Navigation
{
   private final NodeContext<NavigationImpl> context;
   private SiteImpl site;
   private String id;
   private final GateInImpl gateIn;
   private URI uri;
   private String displayName;
   private ResourceBundle bundle;

   public NavigationImpl(SiteImpl site, NodeContext<NavigationImpl> context, GateInImpl gateIn)
   {
      this.context = context;
      this.site = site;
      this.gateIn = gateIn;
   }



   @Override
   public String toString()
   {
      String pageRef = context.getState().getPageRef();
      StringBuilder s = new StringBuilder("Navigation@").append(getId()).append(" URI: ").append(getURI());

      if (pageRef != null)
      {
         s.append("-target->").append(pageRef);
      }

      if (context.getNodeCount() != 0)
      {
         loadChildrenIfNeeded();
         s.append("\n|");
         Iterator<NavigationImpl> children = context.iterator();
         while (children.hasNext())
         {
            s.append("\n+--").append(children.next());
         }
         s.append("\n");
      }

      return s.toString();
   }



   public URI getURI()
   {
      if (uri != null)
      {
         return uri;
      }
      else
      {
         try
         {
            PortalKey key = site.getPortalKey();
            RequestContext requestContext = RequestContext.getCurrentInstance();
            SiteType siteType = SiteType.valueOf(key.getType().toUpperCase());
            String siteName = key.getId();
            NavigationResource navResource = new NavigationResource(siteType, siteName, buildURI().toString());
            NodeURL nodeURL = requestContext.createURL(NodeURL.TYPE, navResource);
            nodeURL.setSchemeUse(true);
            uri = new URI(nodeURL.toString());
         }
         catch (URISyntaxException e)
         {
            throw new RuntimeException(e);
         }
         return uri;
      }
   }

   private StringBuilder buildURI()
   {
      NavigationImpl parent = context.getParentNode();
      if (parent != null)
      {
         StringBuilder builder = parent.buildURI();
         if (builder.length() > 0)
         {
            builder.append('/');
         }
         return builder.append(context.getName());
      }
      else
      {
         return new StringBuilder();
      }
   }

   public Page getTargetPage()
   {
      String pageRef = context.getState().getPageRef();
      if (pageRef != null)
      {
         return site.pages.getPageByRef(pageRef);
      }
      else
      {
         return null;
      }
   }

   public void setTargetPage(Page target)
   {
      if (target != null)
      {
         Site po = target.getSite();
         PortalKey key = SiteImpl.createPortalKey(po.getId());
         String ref = key.getType() + "::" + key.getId() + "::" + target.getName();
         context.setState(context.getState().builder().pageRef(ref).build());
      }
      else
      {

         context.setState(context.getState().builder().pageRef(null).build());
      }
   }

   @Override
   public void setTargetPage(String targetId)
   {
      if (targetId != null)
      {
         context.setState(context.getState().builder().pageRef(targetId).build());
      }
      else
      {

         context.setState(context.getState().builder().pageRef(null).build());
      }
   }

   public Site getSite()
   {
      return site;
   }

   public List<Navigation> getChildren()
   {
      loadChildrenIfNeeded();
      return new LinkedList<Navigation>(context.getNodes());
   }

   @Override
   public int getChildrenCount()
   {
      return context.getNodeCount();
   }

   @Override
   public List<Navigation> getChildren(Range range)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void removeChild(String name)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public Navigation addChild(String name)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   private void loadChildrenIfNeeded()
   {
      if (!context.isExpanded())
      {
         NavigationService service = gateIn.getNavigationService();
         try
         {
            gateIn.begin();
            service.rebaseNode(context, Scope.CHILDREN, null);
         }
         finally
         {
            gateIn.end();
         }
      }
   }

   public Navigation getChild(String name)
   {
      if (name == null)
      {
         return null;
      }
      else
      {
         loadChildrenIfNeeded();
         return context.getNode(name);
      }
   }

   @Override
   public Navigation getParent()
   {
      return context.getParentNode();
   }

   @Override
   public int getIndex()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   public String getId()
   {
      if (id == null)
      {
         id = context.getId();
      }

      return id;
   }

   public String getName()
   {
      return context.getName();
   }



   public String getDisplayName()
   {
      // basically duplicating code from UserNode and PortalRequestContext because we can't use it as is
      if (displayName == null)
      {
         String resolvedLabel = null;

         String id = context.getId();

         if (context.getState().getLabel() != null)
         {
            resolvedLabel = ExpressionUtil.getExpressionValue(getBundle(), context.getState().getLabel());
         }
         else if (id != null)
         {
            Locale userLocale = gateIn.getUserLocale();
            DescriptionService descriptionService = gateIn.getDescriptionService();
            Described.State description = descriptionService.resolveDescription(id, userLocale);
            if (description != null)
            {
               resolvedLabel = description.getName();
            }
         }

         //
         if (resolvedLabel == null)
         {
            resolvedLabel = getName();
         }

         //
         this.displayName = EntityEncoder.FULL.encode(resolvedLabel);
      }
      return displayName;
   }

   @Override
   public void setDisplayName(String displayName)
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
   public <T> T getProperty(PropertyType<T> property)
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void moveDown()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public void moveUp()
   {
      //TODO:
      throw new NotYetImplemented();
   }

   @Override
   public boolean isVisible()
   {
      return !context.isHidden();
   }

   @Override
   public void setVisible(boolean visible)
   {
      context.setHidden(!visible);
   }

   public ResourceBundle getBundle()
   {
      if (bundle == null)
      {
         PortalKey key = site.getPortalKey();
         ExoContainer container = ExoContainerContext.getCurrentContainer();
         ResourceBundleManager rbMgr = (ResourceBundleManager)container.getComponentInstanceOfType(ResourceBundleManager.class);
         Locale locale = gateIn.getUserLocale();
         bundle = rbMgr.getNavigationResourceBundle(
            locale.getLanguage(),
            key.getType(),
            key.getId());

         if (bundle == null)
         {
            bundle = EmptyResourceBundle.INSTANCE;
         }
      }
      return bundle;
   }


   public GateIn getGateIn()
   {
      return gateIn;
   }

   static class NavigationNodeModel implements NodeModel<NavigationImpl>
   {
      private final SiteImpl site;
      private final GateInImpl gateIn;

      NavigationNodeModel(SiteImpl site, GateInImpl gateIn)
      {
         this.site = site;
         this.gateIn = gateIn;
      }

      public NodeContext<NavigationImpl> getContext(NavigationImpl node)
      {
         return node.context;
      }

      public NavigationImpl create(NodeContext<NavigationImpl> context)
      {
         return new NavigationImpl(site, context, gateIn);
      }
   }

}
