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
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.eclipse.ecr.testlib;

import org.eclipse.ecr.runtime.model.RuntimeContext;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


public class OSGiRuntimeTestService extends OSGiRuntimeService {

    public OSGiRuntimeTestService(BundleContext context) {
        super(context);
    }

    @Override
    protected void loadComponents(Bundle bundle, RuntimeContext ctx) throws Exception {
        if (!(bundle instanceof RootRuntimeBundle)) {
            super.loadComponents(bundle, ctx);
        }
    }

}
