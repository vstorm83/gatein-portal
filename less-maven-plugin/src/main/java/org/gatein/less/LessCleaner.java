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
package org.gatein.less;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.gatein.less.model.Module;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 * @goal clean
 * @phase test-compile
 */
public class LessCleaner extends AbstractMojo
{
   
   /** @parameter @require */
   private Module[] modules;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException
   {
      try
      {
         List<String> list = new ArrayList<String>();
         Collections.addAll(list, collectFileImported());
         for(Module module : modules) 
         {
            if(!module.hasExternalImport()) 
               continue;
            
            String dir = module.getHomeDirectory();
            for(String fileName : list) 
            {
               File file = new File(dir + "/" + fileName);
               file.delete();
            }
         }
      }
      catch (IOException e)
      {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }
   
   private String[] collectFileImported() throws IOException 
   {
      File file = new File(System.getProperty("java.io.tmpdir"));
      return file.list(new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            return name.startsWith("gatein") && name.endsWith(".less");
         }
      });
   }
}
