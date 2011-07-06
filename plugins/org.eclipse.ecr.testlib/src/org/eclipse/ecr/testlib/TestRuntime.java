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
 * $Id$
 */

package org.eclipse.ecr.testlib;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.nuxeo.common.utils.FileUtils;
import org.eclipse.ecr.runtime.AbstractRuntimeService;
import org.eclipse.ecr.runtime.Version;
import org.eclipse.ecr.runtime.model.impl.DefaultRuntimeContext;

/**
 * A runtime service used for JUnit tests.
 * <p>
 * The Test Runtime has only one virtual bundle
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class TestRuntime extends AbstractRuntimeService {

    public static final String NAME = "Test Runtime";

    public static final Version VERSION = Version.parseString("1.0.0");

    private static int counter = 0;

    public TestRuntime() {
        super(new DefaultRuntimeContext());
        try {
            workingDir = File.createTempFile("NXTestFramework", generateId());
            workingDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    private static synchronized String generateId() {
        long stamp = System.currentTimeMillis();
        counter++;
        return Long.toHexString(stamp) + '-'
                + System.identityHashCode(System.class) + '.' + counter;
    }

    public void deploy(URL url) throws Exception {
        context.deploy(url);
    }

    public void undeploy(URL url) throws Exception {
        context.undeploy(url);
    }

    @Override
    public synchronized void stop() throws Exception {
        super.stop();
        if (workingDir != null) {
            FileUtils.deleteTree(workingDir);
        }
    }

    @Override
    public void reloadProperties() throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
