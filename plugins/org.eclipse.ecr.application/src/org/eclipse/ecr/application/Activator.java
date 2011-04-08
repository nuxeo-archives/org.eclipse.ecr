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
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.jtajca.NuxeoContainer;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeService;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.nuxeo.common.Environment;
import org.nuxeo.common.jndi.NamingContextFactory;
import org.nuxeo.common.utils.FileUtils;
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
public class Activator implements BundleActivator {

    protected BundleContext context;

    protected Configurator[] configurators;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        initSystemProperties();
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
        beforeStop();
        this.context = null;
        afterStop();
        configurators = null;
        JettyConfigurator.stopServer("nuxeo");
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

    @SuppressWarnings("unchecked")
    protected void initSystemProperties() throws IOException {
        Bundle bundle = context.getBundle();
        Enumeration<URL> urls = bundle.findEntries("/", "system.properties", false);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                InputStream in = urls.nextElement().openStream();
                try {
                    readSystemProperties(in);
                } finally {
                    in.close();
                }
            }
        }
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
        if (Environment.getDefault() == null) {
            String homeDir = System.getProperty("nuxeo.home");
            if (homeDir != null) {
                File home = new File(homeDir);
                home.mkdirs();
                Environment.setDefault(new Environment(home));
            }
        }
        File configDir = Environment.getDefault().getConfig();
        if (!configDir.isDirectory()) {
            File home = Environment.getDefault().getHome();
            new File(home, "data").mkdir();
            new File(home, "log").mkdir();
            new File(home, "tmp").mkdir();
            // unzip configuration if any configuration fragment was deployed
            tryUnzipConfig(new File(home, "config"));
        }
    }

    @SuppressWarnings("unchecked")
    protected void tryUnzipConfig(File configDir) throws IOException {
        Bundle bundle = context.getBundle();
        if (!configDir.isDirectory()) {
            configDir.mkdir();
            Enumeration<URL> urls = bundle.findEntries("config", "*.properties", true);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    copyConfigEntry(urls.nextElement(), configDir);
                }
            }
            urls = bundle.findEntries("config", "*.xml", true);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    copyConfigEntry(urls.nextElement(), configDir);
                }
            }
        }
    }

    private File newConfigFile(File configDir, URL url) {
        String path = url.getPath();
        int i = path.lastIndexOf("/config/");
        if (i == -1) {
            throw new IllegalArgumentException("Excpecting a /config/ path.");
        }
        path = path.substring(i+"/config/".length());
        if (File.separatorChar == '/') {
            return new File(configDir, path);
        }
        String[] ar = StringUtils.split(path, '/', false);
        if (ar.length == 0) {
            throw new IllegalArgumentException("Invalid config file path: "+path);
        }
        StringBuilder buf = new StringBuilder(ar[0]);
        for (i = 1; i<ar.length; i++) {
            buf.append(File.separatorChar).append(ar[i]);
        }
        return new File(configDir, buf.toString());
    }

    private void copyConfigEntry(URL url, File configDir) throws IOException {
        InputStream in = url.openStream();
        try {
            File file = newConfigFile(configDir, url);
            file.getParentFile().mkdirs();
            FileUtils.copyToFile(in, file);
        } finally {
            in.close();
        }
    }

    /**
     * Read a properties file by respecting the order in which properties
     * are declared. Multiline properties or unicode encoding is not supported
     * @param in
     * @return
     * @throws IOException
     */
    private void readSystemProperties(InputStream in) throws IOException {
        Properties sysprops = System.getProperties();
        List<String> lines = FileUtils.readLines(in);
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                int p = line.indexOf('=');
                if (p > -1) {
                    String key = line.substring(0, p).trim();
                    if (!sysprops.containsKey(key)) {
                        String v = line.substring(p+1).trim();
                        v = StringUtils.expandVars(v, sysprops);
                        sysprops.put(key, v);
                    }
                }
            }
        }
    }



}
