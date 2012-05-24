package org.gatein.less;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.juzu.plugin.less.impl.lesser.Compilation;
import org.juzu.plugin.less.impl.lesser.JSR223Context;
import org.juzu.plugin.less.impl.lesser.Lesser;
import org.juzu.plugin.less.impl.lesser.URLLessContext;

/**
 * Says "Hi" to the user.
 * @goal compile
 * @phase compile
 * @requiresDependencyResolution
 */
public class LesserMojo extends AbstractMojo
{
   
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
         for(Module module : modules)
         {
            getLog().info(module.getName());
            getLog().info(module.getHomeDirectory());
            getLog().info(module.getOutput());
            Compilation result = (Compilation)lesser.parse(new URLLessContext(new File(module.getHomeDirectory()).toURI().toURL()), module.getName());
            BufferedWriter writer = new BufferedWriter(new FileWriter(module.getOutput()));
            getLog().info(result.getValue());
            writer.write(result.getValue());
            writer.close();
         }
      }
      catch (Exception e)
      {
         throw new MojoExecutionException(e.getMessage());
      }
   }
   
//   private void collect(File file, final List<File> holder) 
//   {
//      if(file.isDirectory()) 
//      {
//         Collections.addAll(holder, file.listFiles(new FileFilter()
//         {
//            @Override
//            public boolean accept(File file)
//            {
//               if(file.isDirectory()) collect(file, holder);
//               return file.getName().endsWith(".less");
//            }
//         }));
//      }
//   }
}
