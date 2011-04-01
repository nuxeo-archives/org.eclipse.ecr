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

/**
 * Usage:
 * <p>
 * <code>Main [-r repository] [-p profiles] profile target</code>
 * <p>
 * Example:
 * <p>
 * <code>Main -r http://osgi.nuxeo.org/p2/ecr/current/repository -p default target/ecr.zip</pre>
 * <p>
 * If the target path ends with .zip then the product will be zipped.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String repo = "http://osgi.nuxeo.org/p2/ecr/current/repository";
        String profile = null;
        File installDir = null;
        String opt = null;
        for (int i=0; i<args.length; i++) {
            String arg = args[i];
            if (opt != null) {
                if ("-r".equals(opt)) {
                    repo = arg;
                } else if ("-p".equals(opt)) {
                    profile = arg;
                } else {
                    usage("Unknown option "+opt);
                }
                opt = null;
            } else if (arg.startsWith("-")) {
                opt = arg;
            } else {
                if (installDir != null) {
                    usage("too much arguments");
                }
                installDir = new File(arg);
            }
        }
        if (installDir == null) {
            installDir = new File(".");
        }
        if (profile == null) {
            profile = "default";
        }

        String name = installDir.getName();
        boolean zipIt = name.endsWith(".zip");
        if (zipIt) {
            installDir = new File(installDir.getParentFile(), name.substring(0, name.length()-4));
        }

        Installer installer = new Installer(installDir, repo);
        installer.install(profile, zipIt);
    }

    public static void usage(String msg) {
        System.out.println("Syntax Error: "+msg);
        System.out.println("Usage: Main [-p profile] [-r repositoryUrl] [installDirectory]");
        System.exit(1);
    }

}
