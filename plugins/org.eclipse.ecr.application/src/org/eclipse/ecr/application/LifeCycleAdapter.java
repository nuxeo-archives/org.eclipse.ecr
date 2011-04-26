/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.eclipse.ecr.application;

import org.osgi.framework.BundleContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class LifeCycleAdapter implements LifeCycleListener {

    @Override
    public void initialize(BundleContext context) throws Exception {
    }

    @Override
    public void beforeStart(BundleContext context) throws Exception {
    }

    @Override
    public void afterStart(BundleContext context) throws Exception {
    }

    @Override
    public void beforeStop(BundleContext context) throws Exception {
    }

    @Override
    public void afterStop(BundleContext context) throws Exception {
    }

}
