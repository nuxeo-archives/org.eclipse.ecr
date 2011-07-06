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
package org.eclipse.ecr.testlib.runner;

import org.junit.runners.model.FrameworkMethod;

import com.google.inject.Binder;

/**
 * These are the states the runner goes through when using runtime feature:
 * <pre>
 * CREATE FRAMEWORK
 * new feature()        --> constructor
 * COLLECT DEFINED DEPLOYMENTS
 * feature.initialize() --> can be used to configure nuxeo home or register JNDI objects
 * START FRAMEWORK
 * feature.start()
 * CREATE INJECTOR => feature.configure() --> can be used to add guice bindings and to dynamically deploy components using the harness
 * feature.beforeRun()
 * feature.beforeMethodRun()  --> test method interceptor
 * feature.afterMethodRun()   --> test method interceptor
 * feature.afterRun() --> cleanup that require framework to be started
 * STOP FRAMEWORK
 * feature.stop()  --> destructor
 * </pre>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class SimpleFeature implements RunnerFeature {

    @Override
    public void afterRun(FeaturesRunner runner) throws Exception {
    }

    @Override
    public void beforeRun(FeaturesRunner runner) throws Exception {
    }

    @Override
    public void start(FeaturesRunner runner) throws Exception {
    }

    @Override
    public void stop(FeaturesRunner runner) throws Exception {
    }

    @Override
    public void configure(FeaturesRunner runner, Binder binder) {
    }

    @Override
    public void initialize(FeaturesRunner runner)
            throws Exception {
    }

    @Override
    public void afterMethodRun(FeaturesRunner runner, FrameworkMethod method,
            Object test) throws Exception {
    }

    @Override
    public void beforeMethodRun(FeaturesRunner runner, FrameworkMethod method,
            Object test) throws Exception {
    }

}
