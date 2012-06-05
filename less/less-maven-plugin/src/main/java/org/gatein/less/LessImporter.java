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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.gatein.less.model.ImportEntry;
import org.gatein.less.model.Module;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 * @goal import
 * @phase validate
 * @requiresDependencyResolution
 */
public class LessImporter extends AbstractMojo
{
   /** @component */
   private ArtifactFactory artifactFactory;

   /** @component */
   private ArtifactResolver resolver;

   /** @parameter default-value="${localRepository}" */
   private ArtifactRepository localRepository;

   /** @parameter default-value="${project.remoteArtifactRepositories}" */
   private List remoteRepositories;

   /** @parameter default-value="${artifactCoords}" */
   private String artifactCoords;

   /** @parameter @require */
   private Module[] modules;

   private JarFile archFile;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException
   {
      if (!hasImported())
      {
         getLog().warn("No module has been used external importing");
         return;
      }

      Artifact artifact = buildArtifact();
      try
      {
         resolver.resolve(artifact, remoteRepositories, localRepository);
         getLog().info("File: " + artifact.getFile().getAbsolutePath());
         archFile = new JarFile(artifact.getFile());

         Map<String, ImportEntry> importEntries = parseModules();
         for(ImportEntry entry : importEntries.values()) 
         {
            for(String location : entry.getLocation())
            {
               FileOutputStream fos = new FileOutputStream(location + "/" + entry.getName());
               fos.write(entry.getData());
               fos.close();
               createTempFile(entry.getName());
            }
         }
      }
      catch (ArtifactResolutionException e)
      {
         throw new MojoExecutionException(e.getMessage(), e);
      }
      catch (ArtifactNotFoundException e)
      {
         throw new MojoExecutionException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }

   private Map<String, ImportEntry> parseModules() throws IOException
   {
      Map<String, ImportEntry> holder = new HashMap<String, ImportEntry>();
      for (Module module : modules)
      {
         if (!module.hasExternalImport())
            continue;
         
         
         BufferedReader reader = new BufferedReader(new FileReader(module.getHomeDirectory() + "/" + module.getName()));
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            if (line.indexOf("@import") != -1)
            {
               String name = line.substring("@import".length(), line.lastIndexOf(';')).trim();
               name = name.replaceAll("['\"]", "");

               if (!name.startsWith("gatein"))
                  continue;

               ImportEntry entry = holder.get(name);
               if (entry == null)
               {
                  entry = new ImportEntry();
                  entry.setName(name);
               }

               if (entry.getData() == null)
               {
                  for (Enumeration<JarEntry> e = archFile.entries(); e.hasMoreElements();)
                  {
                     JarEntry jarEntry = e.nextElement();
                     String elementName = jarEntry.getName();

                     if(!elementName.endsWith(name)) 
                        continue;
                     
                     entry.setData(writeToByteArrays(jarEntry));
                  }
               }
               
               entry.addLocation(module.getHomeDirectory());
               holder.put(name, entry);
            }
         }
      }
      return holder;
   }
   
   private byte[] writeToByteArrays(JarEntry entry) throws IOException
   {
      InputStream is = archFile.getInputStream(entry);
      BufferedInputStream bis = new BufferedInputStream(is);
      byte[] buff = new byte[256];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for(int l = bis.read(buff); l > -1; l = bis.read(buff))
      {
         baos.write(buff, 0, l);
      }
      return baos.toByteArray();
   }
   
   private boolean createTempFile(String name) throws IOException
   {
      String tempDir = System.getProperty("java.io.tmpdir");
      String fileSeparator = System.getProperty("file.separator");
      File f = new File(tempDir + fileSeparator + name);
      return f.createNewFile();
   }
   
   private boolean hasImported()
   {
      boolean hasImported = false;
      for (Module m : modules)
      {
         hasImported |= m.hasExternalImport();
      }
      return hasImported;
   }

   private Artifact buildArtifact()
   {
      String[] args = artifactCoords.split(":");
      String groupId = args[0];
      String artifactId = args[1];
      String version = args[2];
      String type = args[3];
      Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, null, type);
      return artifact;
   }
}
