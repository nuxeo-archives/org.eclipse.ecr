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

package org.eclipse.ecr.runtime;

import java.net.URL;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;


public class TestTestFramework extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        runtime = Framework.getRuntime();
        assertNotNull(runtime);
    }

    public void testInitialize() {
        assertNotNull(runtime.getHome());
    }

    public void testSetProperty() {
        runtime.getProperties().put("toto", "titi");
        assertEquals("titi", runtime.getProperty("toto"));
    }

    public void testLookupBundleUrl() throws Exception {

        urls = new URL[] {
                new URL("file:/repo/org/nuxeo/nuxeo-runtime-1.4-SNAPSHOT.jar"),
                new URL("file:/projects/nuxeo-core-test/bin/test"),
                new URL("file:/projects/nuxeo-core-test/bin/main"),
                new URL("file:/projects/nuxeo-common/target/test-classes"),
                new URL("file:/projects/nuxeo-common/target/classes"),
        };
        assertEquals(urls[0], lookupBundleUrl("nuxeo-runtime"));
        assertEquals(urls[2], lookupBundleUrl("nuxeo-core-test"));
        assertEquals(urls[4], lookupBundleUrl("nuxeo-common"));
    }

    public void testIsVersionSuffix() {
        assertTrue(isVersionSuffix(""));
        assertTrue(isVersionSuffix("-1.4-SNAPSHOT"));
        assertTrue(isVersionSuffix("-1.3.2"));
        assertTrue(isVersionSuffix("-1.3.2.jar"));
        assertFalse(isVersionSuffix("-api-5.1.1.jar"));
    }

}
