/*
 * Copyright (C) 2013 eXo Platform SAS.
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
  
package org.gatein.tomcat.maven.runner;

import org.apache.tomcat.maven.runner.Tomcat7RunnerCli;

public class ExoTomcat7RunnerCli extends Tomcat7RunnerCli {
    
    public static String JAAS_KEY="java.security.auth.login.config";

    
    public static void main( String[] args ) throws Exception {
        if (System.getProperty(JAAS_KEY) == null) {
            System.setProperty(JAAS_KEY, Thread.currentThread().getContextClassLoader().getResource("conf/jaas.conf").toString());
        }
        
        Tomcat7RunnerCli.main(args);
    }
}
