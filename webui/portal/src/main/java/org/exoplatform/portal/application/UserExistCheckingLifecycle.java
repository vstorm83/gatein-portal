/**
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

package org.exoplatform.portal.application;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.web.application.RequestFailure;
import org.exoplatform.web.login.LogoutControl;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * This app lifecycle must be set after PortalLogoutLifecycle
 *
 */
public class UserExistCheckingLifecycle implements ApplicationLifecycle<WebuiRequestContext> {

    private final Log log = ExoLogger.getLogger(UserExistCheckingLifecycle.class);

    public void onInit(Application app) {
        // do nothing for now
    }

    public void onStartRequest(final Application app, final WebuiRequestContext context) throws Exception {
        String uid = context.getRemoteUser();
        User user = null;
        if (uid != null) {
            ExoContainer exoContainer = app.getApplicationServiceContainer();
            if (exoContainer != null) {
                OrganizationService organizationService = (OrganizationService) exoContainer
                        .getComponentInstanceOfType(OrganizationService.class);
                user = organizationService.getUserHandler().findUserByName(uid, false);
            }

            if (user == null) {
                log.info("user {} not found. Logging out", uid);
                logout(context);
            } else if (!user.isEnabled()) {
                log.info("user {} is disabled. Logging out", uid);
                logout(context);
            }
        }
    }

    private void logout(WebuiRequestContext context) throws Exception {
        LogoutControl.wantLogout();
        PortalRequestContext prContext = (PortalRequestContext) context;
        NodeURL createURL = prContext.createURL(NodeURL.TYPE);
        createURL.setResource(new NavigationResource(SiteType.PORTAL, prContext.getPortalOwner(), null));
        prContext.sendRedirect(createURL.toString());
    }

    public void onFailRequest(Application app, WebuiRequestContext context, RequestFailure failureType) {
    }

    public void onEndRequest(Application app, WebuiRequestContext context) throws Exception {
    }

    public void onDestroy(Application app) {
        // do nothing for now
    }
}
