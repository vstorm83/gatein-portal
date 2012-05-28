package org.gatein.portal.api.impl;

import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.web.url.PortalURL;
import org.exoplatform.web.url.ResourceType;
import org.exoplatform.web.url.URLContext;
import org.exoplatform.web.url.URLFactory;
import org.exoplatform.web.url.URLFactoryPlugin;
import org.exoplatform.web.url.URLFactoryService;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.gatein.common.NotYetImplemented;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class SimpleRequestContext extends RequestContext
{

   /** . */
   private static final URLContext ctx = new URLContext()
   {
      public <R, U extends PortalURL<R, U>> String render(U url)
      {
         PortalURL bu = url;
         if (bu instanceof NodeURL)
         {
            NodeURL nodeURL = (NodeURL)bu;
            NavigationResource res = nodeURL.getResource();
            return "/" + res.getSiteType().getName() + "/" + res.getSiteName() + "/" + res.getNodeURI();
         }
         else
         {
            throw new UnsupportedOperationException();
         }
      }
   };

   public SimpleRequestContext(Application app)
   {
      super(app);
   }

   @Override
   public URLFactory getURLFactory()
   {
      final URLFactoryService ufs = new URLFactoryService();
      ufs.addPlugin(
         new URLFactoryPlugin()
         {
            @Override
            protected ResourceType getResourceType()
            {
               return NodeURL.TYPE;
            }

            @Override
            protected PortalURL newURL(URLContext context)
            {
               return new NodeURL(context);
            }
         }
      );
      return ufs;


//      return new URLFactory()
//      {
//         @Override
//         public <R, U extends PortalURL<R, U>> U newURL(ResourceType<R, U> resourceType, URLContext context) throws NullPointerException
//         {
//            return ufs.newURL(resourceType, context);
////            ResourceType rt = resourceType;
////            if (rt == NodeURL.TYPE)
////            {
////               return (U)new NodeURL(context);
////            }
////            else
////            {
////               return null;
////            }
//         }
//      };
   }

   @Override
   public <R, U extends PortalURL<R, U>> U newURL(ResourceType<R, U> resourceType, URLFactory urlFactory)
   {
      return urlFactory.newURL(resourceType, ctx);
   }

   @Override
   public Orientation getOrientation()
   {
      return Orientation.LT;
   }

   @Override
   public String getRequestParameter(String name)
   {
      return null;
   }

   @Override
   public String[] getRequestParameterValues(String name)
   {
      return null;
   }

   @Override
   public URLBuilder<?> getURLBuilder()
   {
      throw new NotYetImplemented();
   }

   @Override
   public boolean useAjax()
   {
      return false;
   }

   @Override
   public UserPortal getUserPortal()
   {
      return null;
   }
}
