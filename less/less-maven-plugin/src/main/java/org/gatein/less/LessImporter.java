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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.gatein.less.model.ImportEntry;
import org.gatein.less.model.Module;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 * @goal import
 * @phase generate-resources
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
   private List<?> remoteRepositories;

   /** @parameter default-value="${artifactCoords}" */
   private String artifactCoords;

   /** @parameter default-value="${project.build.directory}/${project.build.finalName}" @required */
   private File webappDirectory;

   /** @parameter default-value="${basedir}/src/main/webapp" @required */
   private File warSourceDirectory;

   /** @parameter @require */
   private Module[] modules;

   private JarFile archive;

   private Artifact importArtifact;

   private Pattern gateinImportPattern = Pattern.compile("(.*)(gatein-)(.[^/]*)(\\.less)");

   @Override
   public void execute() throws MojoExecutionException
   {
      if (!hasImported())
      {
         getLog().warn("No module has been used external importing");
         return;
      }
      
      importArtifact = buildArtifact(artifactCoords);

      try
      {
         Utils.rsync(warSourceDirectory, webappDirectory);
         resolver.resolve(importArtifact, remoteRepositories, localRepository);
         archive = new JarFile(importArtifact.getFile());

         Map<String, ImportEntry> importEntries = parseModules();
         for (ImportEntry entry : importEntries.values())
         {
            writeToFile(entry);
         }
      }
      catch (Exception e)
      {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }
   
   private void writeToFile(ImportEntry entry) throws IOException
   {
      for (String location : entry.getLocations())
      {
         String path = webappDirectory.getCanonicalPath() + "/" + location + "/" + entry.getName();
         FileOutputStream fos = new FileOutputStream(Utils.resolveResourcePath(path));
         getLog().info("Write import file to: [" + path + "]");
         fos.write(entry.getData());
         fos.close();
      }

      for (ImportEntry child : entry.getDependencies().values())
      {
         writeToFile(child);
      }
   }

   private Map<String, ImportEntry> parseModules() throws Exception
   {
      Map<String, ImportEntry> holder = new HashMap<String, ImportEntry>();
      for (Module module : modules)
      {
         if (!module.hasExternalImport())
            continue;

         File input = new File(warSourceDirectory.getAbsolutePath() + "/" + module.getInput());
         BufferedReader reader = new BufferedReader(new FileReader(input));
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            if (line.indexOf("@import") == -1)
               continue;

            String name = line.substring("@import".length(), line.lastIndexOf(';')).trim();
            name = name.replaceAll("['\"]", "");

            for (Enumeration<JarEntry> e = archive.entries(); e.hasMoreElements();)
            {
               JarEntry jarEntry = e.nextElement();

               if (!jarEntry.getName().equals(name))
                  continue;

               ImportEntry entry = holder.get(name);
               if (entry == null)
               {
                  entry = new ImportEntry();
                  entry.setName(name);
               }

               if (entry.getData() == null)
               {
                  entry.setData(Utils.writeToByteArrays(jarEntry, archive));
               }
               entry.addLocation(module.getInputDirectory());
               addDependencies(entry, archive);
               holder.put(name, entry);

            }
         }
      }
      return holder;
   }

   private ImportEntry addDependencies(ImportEntry importParent, JarFile archive) throws Exception
   {
      String pwd = importParent.getName().substring(0, importParent.getName().lastIndexOf('/'));
      StringBuilder b = new StringBuilder();

      BufferedReader reader =
         new BufferedReader(new InputStreamReader(new ByteArrayInputStream(importParent.getData())));
      String line = null;
      while ((line = reader.readLine()) != null)
      {
         if (line.indexOf("@import") == -1)
            continue;

         getLog().info("Process line: [" + line + "]");
         String importName = line.substring("@import".length(), line.lastIndexOf(';')).trim();
         importName = importName.replaceAll("['\"]", "");
         String filePath = b.append(pwd).append("/").append(importName).toString();
         b.setLength(0);

         // TODO: resolve path foo/bar/juu/../../A.less
         if (importName.indexOf("../") != -1)
         {
            LinkedList<String> stack = new LinkedList<String>();

            for (String s : filePath.split("/"))
            {
               if (s.equals(".."))
                  stack.removeLast();
               else
                  stack.addLast(s);
            }

            for (int i = 0; i < stack.size(); i++)
            {
               b.append(stack.get(i));

               if (i < stack.size() - 1)
                  b.append("/");
            }

            filePath = b.toString();
         }
         //

         JarEntry jarEntry = archive.getJarEntry(filePath);
         if (jarEntry == null)
            continue;

         ImportEntry importChild = importParent.getDependencies().get(jarEntry.getName());
         if (importChild == null)
         {
            importChild = new ImportEntry();
            importChild.setName(jarEntry.getName());
         }

         if (importChild.getData() == null)
         {
            importChild.setData(Utils.writeToByteArrays(jarEntry, archive));
         }

         importChild.addLocations(importParent.getLocations());
         importParent.addDenepency(importChild);

         // TODO: lookup dependencies recursive
         Matcher matcher = gateinImportPattern.matcher(importChild.getName());
         
         if (matcher.matches())
         {
            addDependencies(importChild, archive);
         }
      }
      return importParent;
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

   private Artifact buildArtifact(String artifactCoords)
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
