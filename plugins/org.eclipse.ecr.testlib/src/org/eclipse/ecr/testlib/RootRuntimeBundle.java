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

import org.nuxeo.osgi.BundleFile;
import org.nuxeo.osgi.BundleImpl;
import org.nuxeo.osgi.OSGiAdapter;
import org.osgi.framework.BundleActivator;

/**
 * @author gracinet
 */
public class RootRuntimeBundle extends BundleImpl {

    public RootRuntimeBundle(OSGiAdapter osgi, BundleFile file,
            ClassLoader loader) {
        super(osgi, file, loader);
    }

    public RootRuntimeBundle(OSGiAdapter osgi, BundleFile file,
            ClassLoader loader, boolean isSystemBundle) {
        super(osgi, file, loader, isSystemBundle);
    }

    @Override
    public BundleActivator getActivator() {
        if (activator == null) {
            activator = new OSGIRuntimeTestActivator();
        }
        return activator;
    }

}
