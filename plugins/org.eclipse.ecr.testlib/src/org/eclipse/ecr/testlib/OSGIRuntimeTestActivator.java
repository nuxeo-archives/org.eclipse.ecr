/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     gracinet
 *
 * $Id$
 */

package org.eclipse.ecr.testlib;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.osgi.OSGiComponentLoader;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeActivator;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeService;
import org.osgi.framework.BundleContext;

/**
 * @author gracinet
 */
public class OSGIRuntimeTestActivator extends OSGiRuntimeActivator {

    private static final Log log = LogFactory.getLog(OSGIRuntimeTestActivator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting Runtime Activator");
        // create the runtime
        runtime = new OSGiRuntimeTestService(context);

        // load main config file if any
        URL config = context.getBundle().getResource("/OSGI-INF/nuxeo.properties");
        if (config != null) {
            System.setProperty(OSGiRuntimeService.PROP_CONFIG_DIR, config.toExternalForm());
        }

        initialize(runtime);
        // start it
        Framework.initialize(runtime);
        // register bundle component loader
        componentLoader = new OSGiComponentLoader(runtime);
        // TODO register osgi services
    }

}
