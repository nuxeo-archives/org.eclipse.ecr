/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.eclipse.ecr.runtime;

import org.eclipse.ecr.runtime.services.deployment.DeploymentService;
import org.eclipse.ecr.runtime.services.event.EventService;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestService extends NXRuntimeTestCase {

    public void testServiceLookup() {
        EventService eventComponent = (EventService) runtime.getComponent(EventService.NAME);
        EventService eventService = runtime.getService(EventService.class);
        assertSame(eventComponent, eventService);

        DeploymentService deploymentComponent = (DeploymentService) runtime.getComponent(DeploymentService.NAME);
        DeploymentService deploymentService = runtime.getService(DeploymentService.class);
        assertSame(deploymentComponent, deploymentService);
    }

}
