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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import juzu.plugin.less.impl.lesser.Compilation;
import juzu.plugin.less.impl.lesser.JSR223Context;
import juzu.plugin.less.impl.lesser.Lesser;
import juzu.plugin.less.impl.lesser.URLLessContext;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.gatein.less.model.Module;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 *
 * @goal compile
 * @phase process-resources
 * @requiresDependencyResolution
 */
public class LessCompiler extends AbstractMojo
{

   /** @parameter default-value="${project.build.directory}/${project.build.finalName}" @required */
   private File webappDirectory;
   
   /**
    * @parameter
    * @required
    */
   private Module[] modules;

   public void execute() throws MojoExecutionException
   {
      try
      {
         Lesser lesser = new Lesser(new JSR223Context());
         for (Module module : modules)
         {
            String input = module.getInput().substring(module.getInput().lastIndexOf('/') + 1);
            String contextPath = webappDirectory.getCanonicalPath() + "/" + module.getInput().substring(0, module.getInput().lastIndexOf(input));
            getLog().info("Less compile phase - input: " + input);
            getLog().info("Less compile phase - context: " + new File(contextPath).toURI().toURL());
            
            URLLessContext context = new URLLessContext(new File(contextPath).toURI().toURL());
            Compilation compilation = (Compilation)lesser.compile(context, input);
            
            BufferedWriter writer = new BufferedWriter(
               new FileWriter(Utils.resolveResourcePath(webappDirectory.getAbsolutePath() + "/"+ module.getOutput())));
            
            getLog().info("The RESULT: \n" + compilation.getValue());
            writer.write(compilation.getValue());
            writer.close();
         }
      }
      catch (Exception e)
      {
         throw new MojoExecutionException(e.getMessage());
      }
   }
}
