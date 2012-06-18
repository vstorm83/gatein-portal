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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 * 
 */
public class Utils
{
   public static File resolveResourcePath(File resource) throws IOException
   {
      return resolveResourcePath(resource.getCanonicalPath());
   }

   public static File resolveResourcePath(String sourcePath) throws IOException
   {
      String separator = System.getProperty("file.separator");
      if("\\".equals(separator)) {
         sourcePath = sourcePath.replaceAll("/", "\\\\");
      }
      
      String directoryPath = sourcePath.substring(0, sourcePath.lastIndexOf(separator));
      String fileName = sourcePath.substring(directoryPath.length() + 1);
      File directory = new File(directoryPath);
      if (!directory.exists())
         directory.mkdirs();
      return new File(directory, fileName);
   }

   public static byte[] writeToByteArrays(JarEntry entry, JarFile archive) throws IOException
   {
      InputStream is = archive.getInputStream(entry);
      BufferedInputStream bis = new BufferedInputStream(is);
      byte[] buff = new byte[256];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for (int l = bis.read(buff); l > -1; l = bis.read(buff))
      {
         baos.write(buff, 0, l);
      }
      return baos.toByteArray();
   }

   public static void rsync(File source, File webapp) throws IOException
   {
      List<File> holder = new ArrayList<File>();
      collectFile(holder, source);
      for(File f : holder)
      {
         String filePath = f.getCanonicalPath().substring(source.getCanonicalPath().length());
         copyFile(f, resolveResourcePath(webapp.getCanonicalPath() + filePath));
      }
   }
   
   private static void copyFile(File src, File des) throws IOException
   {
      FileInputStream is = new FileInputStream(src);
      FileOutputStream os = new FileOutputStream(des);
      try 
      {
         byte[] buff = new byte[1024];
         for(int l = is.read(buff); l > 0; l = is.read(buff))
         {
            os.write(buff, 0, l);
         }
      }
      catch(IOException e)
      { 
         throw e;
      }
      finally
      {
         is.close();
         os.close();
      }
   }
   
   private static void collectFile(final List<File> holder, File file) 
   {
      File[] list = file.listFiles(new FileFilter()
      {
         @Override
         public boolean accept(File f)
         {
            if(f.isDirectory())
            {
               collectFile(holder, f);
            }
            return f.getName().endsWith(".less");
         }
      }); 
      Collections.addAll(holder, list);
   }
}
