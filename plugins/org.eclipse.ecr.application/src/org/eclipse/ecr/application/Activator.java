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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.NamingException;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.jtajca.NuxeoContainer;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeService;
import org.nuxeo.common.Environment;
import org.nuxeo.common.jndi.NamingContextFactory;
import org.nuxeo.common.utils.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * This bundle should be put in a startlevel superior than the one used to start nuxeo bundles.
 * When the bundle is started it will send an application started notification.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Activator implements BundleActivator, Constants {

    private static Activator instance;

    public static Activator getInstance() {
        return instance;
    }

    protected BundleContext context;

    protected Configurator[] configurators;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        instance = this;
        initEnvironment();
        configurators = loadConfigurators();
        beforeStart();
        removeH2Lock();
        startJNDI();
        startRuntime();
        startContainer();
        ((OSGiRuntimeService)Framework.getRuntime()).fireApplicationStarted();
        afterStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        beforeStop();
        this.context = null;
        afterStop();
        configurators = null;
    }

    public BundleContext getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    protected Configurator[] loadConfigurators() throws Exception {
        ArrayList<Configurator> configurators = new ArrayList<Configurator>();
        Bundle bundle = context.getBundle();
        Enumeration<URL> urls = bundle.findEntries("/", ".configurators", false);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                InputStream in = urls.nextElement().openStream();
                BufferedReader reader = new BufferedReader( new InputStreamReader(in, "UTF-8"));
                try {
                    String line = reader.readLine();
                    while (line != null) {
                        line = line.trim();
                        if(line.length() > 0 && !line.startsWith("#")) {
                            Configurator cfg = (Configurator)bundle.loadClass(line).newInstance();
                            cfg.initialize(context);
                            configurators.add(cfg);
                        }
                        line = reader.readLine();
                    }
                } finally {
                    in.close();
                }
            }
        }
        return configurators.toArray(new Configurator[configurators.size()]);
    }

    protected void removeH2Lock() {
        String h2 = System.getProperty("h2.baseDir");
        if (h2 != null) {
            File file = new File(h2);
            file = new File(file, "nuxeo.lucene");
            file = new File(file, "write.lock");
            file.delete();
        }
    }

    protected void startRuntime() throws BundleException {
        try {
            context.getBundle().loadClass("org.eclipse.ecr.runtime.api.Framework");
        } catch (Throwable t) {
            throw new BundleException("Cannot load framework", t);
        }
    }

    protected void startJNDI() throws NamingException {
        NamingContextFactory.install();
    }

    protected void startContainer() throws NamingException {
        NuxeoContainer.install();
    }

    private final void initDefaultProperty(Properties props, String key, String defValue) {
        String v = props.getProperty(key);
        if (v == null) {
            props.setProperty(key, defValue);
        }
    }

    private final File initFileLocation(Properties props, String key, String defValue) {
        String v = props.getProperty(key, defValue);
        v = StringUtils.expandVars(v, props);
        props.setProperty(key, v);
        return new File(v);
    }


    protected void beforeStart() throws Exception {
        for (Configurator c : configurators) {
            c.beforeStart(context);
        }
    }

    protected void afterStart() throws Exception {
        for (Configurator c : configurators) {
            c.afterStart(context);
        }
    }

    protected void beforeStop() throws Exception {
        for (Configurator c : configurators) {
            c.beforeStop(context);
        }
    }

    protected void afterStop() throws Exception {
        for (Configurator c : configurators) {
            c.afterStop(context);
        }
    }

    protected void initEnvironment() throws IOException {
        Properties props = System.getProperties();
        initDefaultProperty(props, ECR_DB, ECR_DB_DEFAULT);
        initDefaultProperty(props, ECR_CONFIGURATOR,
                context.getBundle().getSymbolicName()+":"+ConfigurationProvider.class.getName());
        File file = initFileLocation(props, ECR_HOME_DIR, ECR_HOME_DIR_DEFAULT);
        file.mkdirs();
        Environment env = new Environment(file);
        file = initFileLocation(props, ECR_DATA_DIR, ECR_DATA_DIR_DEFAULT);
        file.mkdirs();
        env.setData(file);
        file = initFileLocation(props, ECR_LOG_DIR, ECR_LOG_DIR_DEFAULT);
        env.setLog(file);
        file.mkdirs();
        env.setConfig(initFileLocation(props, ECR_CONFIG_DIR, ECR_CONFIG_DIR_DEFAULT));
        Environment.setDefault(env);
    }

}
