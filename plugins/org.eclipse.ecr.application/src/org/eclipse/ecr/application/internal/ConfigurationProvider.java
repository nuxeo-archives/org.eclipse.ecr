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
package org.eclipse.ecr.application.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.ecr.application.Activator;
import org.eclipse.ecr.application.Constants;
import org.eclipse.ecr.application.FileIterator;
import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The default configuration provider.
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ConfigurationProvider implements Iterable<URL>, Constants {

    public static final String ECR_CONFIG_SEGMENT = "config";

    protected String configSegmentPattern = "/"+ECR_CONFIG_SEGMENT+"/";

    @Override
    public Iterator<URL> iterator() {
        File root;
        Environment env = Environment.getDefault();
        String uri = env.getProperty(ECR_CONFIG_URI);
        File config = uri != null ? new File(uri) : env.getConfig();
        if (config.isDirectory()) {
            root = config;
        } else {
            File tmp = env.getTemp();
            root = new File(tmp, ECR_CONFIG_SEGMENT);
            env.setConfig(root);
            FileUtils.deleteTree(root);
            root.mkdirs();
            try {
                configureFromFragments(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final String db = env.getProperty(ECR_DATABASE, "h2");
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && pathname.getParent().endsWith("/db")) {
                    // select only the specified database
                    return db.equals(pathname.getName());
                }
                return true;
            }
        };
        FileIterator it = new FileIterator(root, ff);
        it.setSkipDirs(true);
        return FileIterator.asUrlIterator(it);
    }

    protected void configureFromFragments(File root) throws IOException {
        unzipConfig(Activator.getInstance().getContext(), root);
    }

    @SuppressWarnings("unchecked")
    private void unzipConfig(BundleContext context, File configDir) throws IOException {
        Bundle bundle = context.getBundle();
        configDir.mkdir();
        Enumeration<URL> urls = bundle.findEntries(ECR_CONFIG_SEGMENT, "*.*", true);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                copyConfigEntry(urls.nextElement(), configDir);
            }
        }
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

    private File newConfigFile(File configDir, URL url) {
        String path = url.getPath();
        int i = path.lastIndexOf(configSegmentPattern);
        if (i == -1) {
            throw new IllegalArgumentException("Excpecting a "+configSegmentPattern+" segment in path.");
        }
        path = path.substring(i+configSegmentPattern.length());
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


}
