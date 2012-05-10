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
package org.exoplatform.portal.resource.less;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.asual.lesscss.LessOptions;

/**
 * @author <a href="mailto:khoi.nguyen@exoplatform.com">Khoi NGUYEN DUC</a>
 * May 9, 2012
 */
public class LessFilter implements Filter
{
   private LessEngine engine;

   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {
      LessOptions options = new LessOptions();
      options.setCss(true);
      engine = new LessEngine(options);
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      byte[] content = null;
      response.setContentType("text/css;charset=UTF-8");
      String uri = ((HttpServletRequest) request).getRequestURI().toString();
      URL url = new URL(uri);
      try
      {
         String str = engine.compile(url, false);
         content = str.getBytes();
      }
      catch (LessException e)
      {
         e.printStackTrace();
      }
      response.setContentLength(content.length);
      response.getOutputStream().write(content);
      response.getOutputStream().flush();
      response.getOutputStream().close();

   }

   @Override
   public void destroy()
   {
      // TODO Auto-generated method stub

   }

}
