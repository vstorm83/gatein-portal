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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.gatein.less.model.Module;

import com.asual.lesscss.LessEngine;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 *
 * @goal compile
 * @phase compile
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
         LessEngine engine = new LessEngine();
         for (Module module : modules)
         {
            File input = new File(webappDirectory.getAbsoluteFile() + "/" + module.getInput());
            String result = engine.compile(input);
            BufferedWriter writer = new BufferedWriter(
               new FileWriter(Utils.resolveResourcePath(webappDirectory.getAbsolutePath() + "/"+ module.getOutput())));
            
            getLog().info("The RESULT: \n" + result);
            writer.write(result);
            writer.close();
         }
      }
      catch (Exception e)
      {
         throw new MojoExecutionException(e.getMessage());
      }
   }
}
