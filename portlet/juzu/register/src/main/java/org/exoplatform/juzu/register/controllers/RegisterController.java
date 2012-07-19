/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.juzu.register.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;

import org.exoplatform.juzu.register.Flash;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.NaturalLanguageValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.exoplatform.webui.form.validator.UsernameValidator;
import org.exoplatform.webui.form.validator.Validator;
import juzu.Action;
import juzu.Controller;
import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.io.Stream;
import juzu.plugin.ajax.Ajax;
import juzu.portlet.JuzuPortlet;
import juzu.template.Template;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 */
public class RegisterController extends Controller
{

   @Inject
   @Path("main.gtmpl")
   org.exoplatform.juzu.register.templates.main main;
   
   @Inject
   @Path("edit.gtmpl")
   Template edit;

   @Inject
   OrganizationService organizationService;

   @Inject
   Flash flash;
   
   @Inject
   PortletPreferences preferences;

   private Map<String, Validator> validators;

   public RegisterController()
   {
      validators = new HashMap<String, Validator>();
      validators.put(FieldNameConstant.USER_NAME, new UsernameValidator(3, 30));
      validators.put(FieldNameConstant.PASSWORD, new PasswordStringLengthValidator(6, 30));
      validators.put(FieldNameConstant.NAME, new NaturalLanguageValidator());
      validators.put(FieldNameConstant.EMAIL_ADDRESS, new EmailAddressValidator());
   }

   @View
   public void index()
   {
		if (renderContext.getProperty(JuzuPortlet.PORTLET_MODE) == PortletMode.VIEW) 
		{
			main.with().useCaptcha(Boolean.parseBoolean(preferences.getValue("captcha", "true"))).render();
		}
		else if (renderContext.getProperty(JuzuPortlet.PORTLET_MODE) == PortletMode.EDIT)
		{
			edit.render();
		}
   }
   
   @Action
   public Response edit(String captcha) throws Exception {
   	preferences.setValue("captcha", captcha == null ? "false" : "true");
   	preferences.store();
   	return RegisterController_.index().with(JuzuPortlet.PORTLET_MODE, PortletMode.VIEW);
   }
   
   private Response.Content<Stream.Char> createJSON(final Map<String, String> data)
   {
      Response.Content<Stream.Char> json = new Response.Content<Stream.Char>(200, Stream.Char.class)
      {

         @Override
         public String getMimeType()
         {
            return "application/json";
         }

         @Override
         public void send(Stream.Char stream) throws IOException
         {
            stream.append("{");
            Iterator<Map.Entry<String, String>> i = data.entrySet().iterator();
            while(i.hasNext())
            {
               Map.Entry<String, String> entry = i.next();
               stream.append("\"" + entry.getKey() + "\"");
               stream.append(":");
               stream.append("\"" + entry.getValue() + "\"");
               if(i.hasNext())
               {
                  stream.append(",");
               }
            }
            stream.append("}");
         }
      };
      return json;
   }

   @Resource
   public Response serveImage()
   {
      Captcha captcha = new Captcha.Builder(200, 50).addText().gimp().addNoise().addBackground().build();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      CaptchaServletUtil.writeImage(baos, captcha.getImage());

      flash.setCaptcha(captcha.getAnswer());
      return Response.ok("image/png", new ByteArrayInputStream(baos.toByteArray()));
   }

   @Ajax
   @Resource
   public Response validate(String name, String value) throws Exception
   {
      Validator validator = null;
      if (name.equals("password") || name.equals("confirmPassword"))
      {
         validator = validators.get(FieldNameConstant.PASSWORD);
      }
      else if (name.equals("firstName") || name.equals("lastName"))
      {
         validator = validators.get(FieldNameConstant.NAME);
      }
      else
      {
         validator = validators.get(name);
      }

      UIFormStringInput input = new UIFormStringInput(name, value);
      Map<String, String> result = new HashMap<String, String>();
      result.put("msg", "Ok");
      result.put("return", "true");
      
      if(value.trim().isEmpty()) {
         result.put("msg", "Please correct the error");
         result.put("return", "false");
      }
      
      if (validator == null) 
      {
         if (name.equals("captcha"))
         {
            if (value.equals(flash.getCaptcha()))
            {
               return createJSON(result);
            }
            else
            {
               result.put("msg", "captcha incorrect");
               result.put("return", "false");
               return createJSON(result);
            }
         }
         return null;
      }

      UserHandler userHandler = organizationService.getUserHandler();

      if (name.equals(FieldNameConstant.USER_NAME) && userHandler.findUserByName(value) != null)
      {
         result.put("msg", "This username already exists");
         result.put("return", "false");
      }
      else if (name.equals(FieldNameConstant.EMAIL_ADDRESS))
      {
         Query query = new Query();
         query.setEmail(value);
         if (userHandler.findUsersByQuery(query).getSize() > 0)
         {
            result.put("msg", "Email " + value + " already existed");
            result.put("return", "false");
         }
      }

      try
      {
         validator.validate(input);
      }
      catch (MessageException e)
      {
         result.put("msg", "Please correct the error");
         result.put("return", "false");
      }
      return createJSON(result);
   }

   @Ajax
   @Resource
   public Response saveUser(String username, String password, String confirmPassword, String firstName,
      String lastName, String emailAddress, String captcha)
   {
      boolean useCaptcha = Boolean.parseBoolean(preferences.getValue("captcha", "true"));
      try
      {
         if (!captcha.equals(flash.getCaptcha()) && useCaptcha)
         {
            return Response.ok("<strong>Captcha is incorrect</strong>");
         }
         UserHandler userHandler = organizationService.getUserHandler();

         User user = userHandler.createUserInstance(username);
         user.setPassword(password);
         user.setFirstName(firstName);
         user.setLastName(lastName);
         user.setEmail(emailAddress);

         userHandler.createUser(user, true);// Broadcast user creaton event
         return Response.ok("<script type='text/javascript'>alert('Username " + username
            + " create successfully');window.location.href='/'</script>");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return Response.ok(e.getMessage());
      }
   }
}