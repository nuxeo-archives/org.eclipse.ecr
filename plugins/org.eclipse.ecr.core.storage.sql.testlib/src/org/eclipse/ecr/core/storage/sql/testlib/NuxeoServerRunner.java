/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.eclipse.ecr.core.storage.sql.testlib;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.JUnitCore;

/**
 * Runs a Nuxeo server based on the unit tests configuration. Wait for a
 * connection on a given port to stop it.
 * <p>
 * Args: -p SHUTDOWNPORT
 */
public class NuxeoServerRunner {

    private static final Log log = LogFactory.getLog(NuxeoServerRunner.class);

    public static int shutdownPort = 4444;

    public static void getPortFromArgs(String[] args) {
        if (args.length == 2 && "-p".equals(args[0])) {
            try {
                shutdownPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                // use default port
            }
        }
        String msg = "Using server shutdown port " + shutdownPort;
        log.info(msg);
        // note that System.out is closed when running from ant spawn
        System.out.println(msg);
    }

    public static void main(String[] args) {
        try {
            getPortFromArgs(args);
            JUnitCore.runClasses(ToRun.class);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public static class ToRun extends TXSQLRepositoryTestCase {

        public static final String BUNDLE = "org.eclipse.ecr.core.storage.sql.testlib";

        @Override
        protected void deployRepositoryContrib() throws Exception {
            deployContrib(BUNDLE,
                    "OSGI-INF/test-backend-core-types-contrib.xml");

            if (database instanceof DatabaseH2) {
                String contrib = "OSGI-INF/test-server-pooling-h2-contrib.xml";
                deployContrib(BUNDLE, contrib);
            } else {
                super.deployRepositoryContrib();
            }
        }

        // wait until connection on PORT
        public void test() throws Exception {
            new ServerSocket(shutdownPort).accept();
        }
    }

    public static class Stopper {

        public static void main(String[] args) throws Exception {
            getPortFromArgs(args);
            new Socket("127.0.0.1", shutdownPort);
        }

    }

}
