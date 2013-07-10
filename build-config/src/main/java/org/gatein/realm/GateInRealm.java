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

package org.gatein.realm;

import javax.security.auth.callback.CallbackHandler;

import java.io.File;
import java.net.URL;
import java.security.Principal;

import org.apache.catalina.realm.JAASRealm;

public class GateInRealm extends JAASRealm {

    public static String JAAS_KEY = "java.security.auth.login.config";

    public static String DEFAULT_PATH = "/conf/jaas.conf";

    @Override
    protected Principal authenticate(String username, CallbackHandler callbackHandler) {
        if (System.getProperty(JAAS_KEY) == null) {
            // Find in default location
            String path = System.getProperty("catalina.base") + DEFAULT_PATH;

            File configFile = new File(path);
            if (configFile.exists()) {
                System.setProperty(JAAS_KEY, path);
            } else {
                // extract default config in jar
                URL defConfig = Thread.currentThread().getContextClassLoader().getResource("jaas.conf");
                if (defConfig != null) {
                    System.setProperty(JAAS_KEY, defConfig.toString());
                }
            }
        }
        return super.authenticate(username, callbackHandler);
    }
}
