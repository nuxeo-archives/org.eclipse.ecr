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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;

import org.eclipse.ecr.application.internal.ConfigurationProvider;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.jtajca.NuxeoContainer;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeService;
import org.nuxeo.common.Environment;
import org.nuxeo.common.jndi.NamingContextFactory;
import org.nuxeo.common.utils.Vars;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

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

    protected LifeCycleListener[] listeners;

    protected ServiceReference pkgAdmin;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        instance = this;

        pkgAdmin = context.getServiceReference(PackageAdmin.class.getName());

        // initialize environment
        Environment.setDefault(createEnvironment());

        listeners = loadListeners();
        beforeStart();
        removeH2Lock();
        startJNDI();
        startRuntime();
        startContainer();
        ((OSGiRuntimeService)Framework.getRuntime()).fireApplicationStarted();
        // install extra plugins if any
        installPlugins();
        afterStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        beforeStop();
        pkgAdmin = null;
        this.context = null;
        afterStop();
        listeners = null;
    }

    public BundleContext getContext() {
        return context;
    }

    protected LifeCycleListener[] loadListeners() throws Exception {
        PackageAdmin pa = (PackageAdmin)context.getService(pkgAdmin);
        try {
            Bundle[] fragments = pa.getFragments(context.getBundle());
            ArrayList<LifeCycleListener> listeners = new ArrayList<LifeCycleListener>();
            if (fragments != null) {
                Bundle bundle = context.getBundle();
                for (Bundle fragment : fragments) {
                    String cname = (String)fragment.getHeaders().get(ECR_APPLICATION_LISTENER);
                    if (cname != null) {
                        LifeCycleListener listener = (LifeCycleListener)bundle.loadClass(cname).newInstance();
                        listener.initialize(context);
                        listeners.add(listener);
                    }
                }
            }
            return listeners.toArray(new LifeCycleListener[listeners.size()]);
        } finally {
            context.ungetService(pkgAdmin);
        }
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

    protected void beforeStart() throws Exception {
        for (LifeCycleListener c : listeners) {
            c.beforeStart(context);
        }
    }

    protected void afterStart() throws Exception {
        for (LifeCycleListener c : listeners) {
            c.afterStart(context);
        }
    }

    protected void beforeStop() throws Exception {
        for (LifeCycleListener c : listeners) {
            c.beforeStop(context);
        }
    }

    protected void afterStop() throws Exception {
        for (LifeCycleListener c : listeners) {
            c.afterStop(context);
        }
    }

    protected Map<String,String> createDefaultConfiguration() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(ECR_APP_ID, "default");
        map.put(ECR_DATABASE, "h2");
        map.put(ECR_HOME_DIR, "${user.home}/.ecr");
        map.put(ECR_DATA_DIR, "${"+ECR_HOME_DIR+"}/data");
        map.put(ECR_LOG_DIR, "${"+ECR_HOME_DIR+"}/log");
        map.put(ECR_CONFIG_URI, "${"+ECR_HOME_DIR+"}/config");
        map.put(ECR_CONFIG_PROVIDER, "org.eclipse.ecr.application:"+ConfigurationProvider.class.getName());
        return map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Environment createEnvironment() throws Exception {
        // enable OSGi services
        System.setProperty("ecr.osgi.services", "true");
        Map<String,String> vars = createDefaultConfiguration();
        String configUri = System.getProperty(ECR_APP_CONFIG);
        if (configUri != null) {
            Properties props = loadConfiguration(configUri);
            vars.putAll((Map)props);
        }
        // override using system properties
        String v = System.getProperty(ECR_APP_ID);
        if (v != null) {
            vars.put(ECR_APP_ID, v);
        }
        v = System.getProperty(ECR_DATABASE);
        if (v != null) {
            vars.put(ECR_DATABASE, v);
        }
        v = System.getProperty(ECR_CONFIG_PROVIDER);
        if (v != null) {
            vars.put(ECR_CONFIG_PROVIDER, v);
        }
        v = System.getProperty(ECR_CONFIG_URI);
        if (v != null) {
            vars.put(ECR_CONFIG_URI, v);
        }
        v = System.getProperty(ECR_HOME_DIR);
        if (v != null) {
            vars.put(ECR_HOME_DIR, v);
        }
        v = System.getProperty(ECR_DATA_DIR);
        if (v != null) {
            vars.put(ECR_DATA_DIR, v);
        }
        v = System.getProperty(ECR_LOG_DIR);
        if (v != null) {
            vars.put(ECR_LOG_DIR, v);
        }

        // expand properties
        vars = Vars.expand(vars, new Vars.Resolver() {
            @Override
            public String get(String key) {
                return System.getProperty(key);
            }
        });

        return createEnvironment(vars);
    }

    @SuppressWarnings("unchecked")
    protected Environment createEnvironment(Map<String,String> vars) throws Exception {
        String v = vars.get(ECR_HOME_DIR);
        File home = v == null ? new File(System.getProperty("user.home"), ".ecr") : new File(v);
        home.mkdirs();
        Environment env = new Environment(home);
        v = vars.get(ECR_DATA_DIR);
        if (v != null) {
            env.setData(new File(v));
        }
        v = vars.get(ECR_LOG_DIR);
        if (v != null) {
            env.setLog(new File(v));
        }
        env.getData().mkdirs();
        env.getLog().mkdirs();
        env.getProperties().putAll(vars);
        String classRef = vars.get(ECR_CONFIG_PROVIDER);
        if (classRef != null) {
            env.setConfigurationProvider((Iterable<URL>)newInstance(classRef));
        }
        System.setProperty(ECR_HOME_DIR, home.getAbsolutePath());
        System.setProperty(ECR_DATA_DIR, env.getData().getAbsolutePath());
        System.setProperty(ECR_LOG_DIR, env.getLog().getAbsolutePath());
        return env;
    }

    protected static Properties loadConfiguration(String configUri) throws IOException {
        InputStream in;
        if (configUri.indexOf(':') != -1) {
            in = new URL(configUri).openStream();
        } else {
            in = new FileInputStream(new File(configUri));
        }
        try {
            Properties props = new Properties();
            props.load(in);
            return props;
        } finally {
            in.close();
        }
    }

    public void installPlugins() throws IOException, BundleException {
        File root = new File(Environment.getDefault().getHome(), "plugins");
        if (!root.isDirectory()) {
            return;
        }
        FileIterator plugins = new FileIterator(root, new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return pathname.getPath().endsWith(".jar");
            }
        });
        plugins.setSkipDirs(true);
        Bundle[] bundles = context.getBundles();
        Map<String,Bundle> installed = new HashMap<String, Bundle>();
        for (Bundle bundle : bundles) {
            installed.put(bundle.getLocation(), bundle);
        }
        while (plugins.hasNext()) {
            File f = plugins.next();
            String location = f.getAbsolutePath();
            if (!installed.containsKey(location)) {
                InputStream in = new FileInputStream(f);
                try {
                    Bundle b = context.installBundle(location, in);
                    b.start(Bundle.START_ACTIVATION_POLICY | Bundle.START_TRANSIENT);
                } finally {
                    in.close();
                }
            }
        }
    }

    /**
     * Load a class from another bundle given its reference as <code>bundleSymbolicName:className</code>
     * If no <code>bundleSymbolicName:</code> prefix is given then a classForName will be done
     * @param ref
     * @return
     */
    public Class<?> loadClass(String ref) throws Exception {
        int i = ref.indexOf(':');
        if (i == -1) {
            return Class.forName(ref);
        }
        return loadClass(ref.substring(0, i), ref.substring(i+1));
    }

    public Class<?> loadClass(String bundleName, String className) throws Exception {
        Bundle bundle = getBundle(bundleName);
        if (bundle == null) {
            throw new ClassNotFoundException("No bundle found with name: "+bundleName+". Unable to load class "+className);
        }
        return bundle.loadClass(className);
    }

    public Object newInstance(String ref) throws Exception {
        return loadClass(ref).newInstance();
    }

    public Object newInstance(String bundleName, String className) throws Exception {
        return loadClass(bundleName, className).newInstance();
    }

    public Bundle getBundle(String name) {
        if (pkgAdmin == null) {
            return null;
        }
        PackageAdmin pa = (PackageAdmin)context.getService(pkgAdmin);
        Bundle[] bundles = pa.getBundles(name, null);
        context.ungetService(pkgAdmin);
        return bundles == null ? null : bundles[0];
    }

}
