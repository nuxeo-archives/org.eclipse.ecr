/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Leroy Merlin (http://www.leroymerlin.fr/) - initial implementation
 * $Id$
 */
package org.eclipse.ecr.runtime.test.runner;

import static org.junit.Assert.assertNotNull;

import org.eclipse.ecr.testlib.runner.Features;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.Jetty;
import org.eclipse.ecr.testlib.runner.JettyFeature;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;

import com.google.inject.Inject;


@Ignore("No yet working")
@RunWith(FeaturesRunner.class)
@Features(JettyFeature.class)
@Jetty(port=9090)
public class JettyTest {

    @Inject
    Server server;

    @Test
    public void jettyComponentIsDeployed() {
        assertNotNull(server);
    }

}
