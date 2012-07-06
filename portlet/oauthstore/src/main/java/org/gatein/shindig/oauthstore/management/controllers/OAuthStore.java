/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.gatein.shindig.oauthstore.management.controllers;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.gadget.core.OAuthStoreConsumer;
import org.exoplatform.portal.gadget.core.OAuthStoreConsumerService;
import org.gatein.shindig.oauthstore.management.Session;
import juzu.Action;
import juzu.Controller;
import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.impl.compiler.BaseProcessor;
import juzu.impl.utils.Logger;
import juzu.plugin.ajax.Ajax;

import java.util.List;

import javax.inject.Inject;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OAuthStore extends Controller
{
   private final Logger log = BaseProcessor.getLogger(OAuthStore.class);

   @Inject
   @Path("oauthlist.gtmpl")
   org.gatein.shindig.oauthstore.management.templates.oauthlist oauthList;

   @Inject
   @Path("newconsumer.gtmpl")
   org.gatein.shindig.oauthstore.management.templates.newconsumer newConsumer;

   @Inject
   @Path("consumerdetail.gtmpl")
   org.gatein.shindig.oauthstore.management.templates.consumerdetail consumerDetail;

   @Inject
   @Path("mappings.gtmpl")
   org.gatein.shindig.oauthstore.management.templates.mappings mappings;
   
   @Inject
   Session session;

   //@Inject
   String message;

   @View
   public void index()
   {
      OAuthStoreConsumerService store =
         (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
            OAuthStoreConsumerService.class);
      List<OAuthStoreConsumer> allConsumers = store.getAllConsumers();
      oauthList.with().allConsumers(allConsumers).render();
   }

   @View
   public void addNewConsumer()
   {
      newConsumer.with().session(session).message(message).render();
   }
   
   @View
   public void consumerDetail()
   {
      consumerDetail.with().consumer(session.getConsumer()).render();
   }

   @Action
   public Response deleteConsumer(String keyName)
   {
      OAuthStoreConsumerService dataService =
         (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
            OAuthStoreConsumerService.class);
      dataService.removeConsumer(keyName);
      return OAuthStore_.index();
   }

   @Action
   public Response showAddNewConsumer()
   {
      return OAuthStore_.addNewConsumer();
   }

   @Action
   public Response showConsumerDetail(String keyName)
   {
      OAuthStoreConsumerService dataService =
         (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
            OAuthStoreConsumerService.class);
      session.setConsumer(dataService.getConsumer(keyName));
      return OAuthStore_.consumerDetail();
   }

   @Action
   public Response deleteMapping(String keyName, String gadgetUri)
   {
      OAuthStoreConsumerService dataService =
         (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
            OAuthStoreConsumerService.class);
      dataService.removeMappingKeyAndGadget(keyName, gadgetUri);
      session.setConsumer(dataService.getConsumer(keyName));
      return OAuthStore_.consumerDetail();
   }

   @Action
   public Response submitNewConsumer(String keyName, String consumerKey, String consumerSecret, String keyType)
   {
      if (keyName == "" || consumerKey == "" || consumerSecret == "" || keyType == "")
      {
         message = "You must fill all fields";
         session = null;
         return OAuthStore_.addNewConsumer();
      }

      if (keyType.equals("RSA_PRIVATE"))
      {
         consumerSecret.replaceAll("-----[A-Z ]*-----", "").replace("\n", "");
      }

      OAuthStoreConsumerService dataService =
         (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
            OAuthStoreConsumerService.class);
      OAuthStoreConsumer consumer = new OAuthStoreConsumer(keyName, consumerKey, consumerSecret, keyType, null);
      try
      {
         dataService.storeConsumer(consumer);
      }
      catch (Exception e)
      {
         log.log("Can not store consumer with key name: " + consumer.getKeyName() + e.getMessage());
      }

      return OAuthStore_.index();
   }

   @Ajax
   @Resource
   public void addGadgetURIToKey(String gadgetURI, String keyName)
   {
      if (gadgetURI != null && keyName != null)
      {
         OAuthStoreConsumerService store =
            (OAuthStoreConsumerService)PortalContainer.getInstance().getComponentInstanceOfType(
               OAuthStoreConsumerService.class);
         try
         {
            store.addMappingKeyAndGadget(keyName, gadgetURI);
         }
         catch (Exception e)
         {
            log.log("Can not add map key:" + keyName + " and gadget uri:" + gadgetURI + e.getMessage());
         }
         mappings.with().consumer(store.getConsumer(keyName)).render();
      }
   }
}
