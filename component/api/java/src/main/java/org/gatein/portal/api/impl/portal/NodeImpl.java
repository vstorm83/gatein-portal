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

import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.gatein.api.exception.EntityNotFoundException;
import org.gatein.api.portal.Label;
import org.gatein.api.portal.Node;
import org.gatein.api.portal.Page;
import org.gatein.common.NotYetImplemented;
import org.gatein.portal.api.impl.GateInImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

import static org.gatein.common.util.ParameterValidation.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class NodeImpl implements Node
{
   final NodeContext<Node> context;

   private final Id id;
   private final SiteImpl site;
   private final GateInImpl gateIn;

   NodeImpl(SiteImpl site, GateInImpl gateIn, NodeContext<Node> context)
   {
      this.site = site;
      this.gateIn = gateIn;
      this.context = context;

      String buildPath = buildPath().toString();
      this.id = Node.Id.create(site.getId(), buildPath.split("/"));
   }

   @Override
   public Id getId()
   {
      return id;
   }

   @Override
   public String getName()
   {
      return context.getName();
   }

   @Override
   public Node getParent()
   {
      return context.getParentNode();
   }

   @Override
   public Node getChild(String name)
   {
      throwIllegalArgExceptionIfNull(name, "name");

      loadChildren();
      return context.getNode(name);
   }

   @Override
   public Node getDescendant(String... path)
   {
      throwIllegalArgExceptionIfNullOrEmpty(path, "path");

      if (path.length == 1) return getChild(path[0]);

      //TODO: Implement
      throw new NotYetImplemented();
   }

   @Override
   public void removeChild(String name) throws EntityNotFoundException
   {
      throwIllegalArgExceptionIfNull(name, "name");

      loadChildren();

      boolean removed = context.removeNode(name);
      if (!removed) throw new EntityNotFoundException("Node " + this + " does not have child of name " + name);
   }

   @Override
   public void removeDescendant(String... path) throws EntityNotFoundException
   {
      throwIllegalArgExceptionIfNullOrEmpty(path, "path");

      if (path.length == 1)
      {
         removeChild(path[0]);
         return;
      }

      //TODO: Implement
      throw new NotYetImplemented();
   }

   @Override
   public Node addChild(String name)
   {
      loadChildren();

      return context.add(null, name).getNode();
   }

   @Override
   public int getCount()
   {
      return context.getNodeCount();
   }

   @Override
   public URI getURI()
   {
      try
      {
         SiteKey siteKey = site.getSiteKey();
         RequestContext requestContext = RequestContext.getCurrentInstance();
         NavigationResource navResource = new NavigationResource(siteKey, id.getPathAsString());
         NodeURL nodeURL = requestContext.createURL(NodeURL.TYPE, navResource);
         nodeURL.setSchemeUse(true);
         return new URI(nodeURL.toString());
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Label getLabel()
   {
      return new LabelImpl(context.getState().getLabel());
   }

   @Override
   public String getIcon()
   {
      return context.getState().getIcon();
   }

   @Override
   public void setIcon(String icon)
   {
      context.setState(new NodeState.Builder(context.getState()).icon(icon).build());
   }

   @Override
   public Page getPage()
   {
      //TODO: Implement
      throw new NotYetImplemented();
   }

   @Override
   public Page.Id getPageReference()
   {
      //TODO: Implement
      throw new NotYetImplemented();
   }

   @Override
   public void setPageReference(Page.Id pageId)
   {
      //TODO: Implement
      throw new NotYetImplemented();
   }

   @Override
   public Visibility getVisibility()
   {
      switch (context.getState().getVisibility())
      {
         case DISPLAYED:
            return Visibility.VISIBLE;
         case HIDDEN:
            return Visibility.HIDDEN;
         case TEMPORAL:
            return Visibility.PUBLICATION;
         case SYSTEM:
            return Visibility.SYSTEM;
         default:
            throw new RuntimeException("Cannot map " + context.getState().getVisibility() + " to a valid visibility.");
      }
   }

   @Override
   public Date getStartPublicationDate()
   {
      long startPublicationTime = context.getState().getStartPublicationTime();
      return startPublicationTime != -1 ? new Date(startPublicationTime) : null;
   }

   @Override
   public Date getEndPublicationDate()
   {
      long endPublicationTime = context.getState().getEndPublicationTime();
      return endPublicationTime != -1 ? new Date(endPublicationTime) : null;
   }

   @Override
   public void setPublication(Date start, Date end)
   {
      throwIllegalArgExceptionIfNull(start, "start date");
      throwIllegalArgExceptionIfNull(start, "end date");

      NodeState state = new NodeState.Builder(context.getState())
         .startPublicationTime(start.getTime())
         .endPublicationTime(end.getTime()).build();

      context.setState(state);
   }

   @Override
   public void setPublication(Date start)
   {
      throwIllegalArgExceptionIfNull(start, "start");

      NodeState state = new NodeState.Builder(context.getState())
         .startPublicationTime(start.getTime())
         .endPublicationTime(-1).build();

      context.setState(state);
   }

   @Override
   public void removePublication()
   {
      NodeState state = new NodeState.Builder(context.getState())
         .startPublicationTime(-1)
         .endPublicationTime(-1).build();

      context.setState(state);
   }

   @Override
   public void setVisible(boolean visible)
   {
      NodeState state = new NodeState.Builder(context.getState())
         .visibility(org.exoplatform.portal.mop.Visibility.DISPLAYED).build();

      context.setState(state);
   }

   @Override
   public Iterator<Node> iterator()
   {
      loadChildren();
      return context.iterator();
   }

   @Override
   public String toString()
   {
      return id.toString();
   }

   private void loadChildren()
   {
      if (!context.isExpanded())
      {
         gateIn.getNavigationService().rebaseNode(context, Scope.CHILDREN, null);
      }
   }

   private StringBuilder buildPath()
   {
      NodeImpl parent = (NodeImpl) context.getParentNode();
      if (parent != null)
      {
         StringBuilder builder = parent.buildPath();
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
}
