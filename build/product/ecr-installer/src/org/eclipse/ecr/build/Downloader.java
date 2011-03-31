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
package org.eclipse.ecr.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Downloader {

    protected ExecutorService pool;

    public Downloader() {
        this (4);
    }

    public Downloader(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void download(final URL url, final File toFile) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Downloading "+toFile.getName());
                try {
                    InputStream in = url.openStream();
                    try {
                        Utils.writeTo(in, toFile);
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void awaitTermination() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.HOURS);
    }

    public void dispose() {
        pool.shutdown();
    }
}
