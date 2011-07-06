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

import junit.framework.AssertionFailedError;

import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class RuntimeInitializationTest extends NXRuntimeTestCase {

    public void testContributions() throws Exception {
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp1.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp2.xml");
    }

    // Deactivated for now since duplicate contributions are still allowed.
    public void XXXtestContributionsWithDuplicateComponent() throws Exception {
        deployContrib("org.eclipse.ecr.runtime.test", "MyComp1.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "MyComp2.xml");
        boolean success = false;
        try {
            deployContrib("org.eclipse.ecr.runtime.test", "CopyOfMyComp2.xml");
            success = true;
        } catch (AssertionFailedError e) {
            // OK.
        }
        assertFalse("An exception should have been raised.", success);
    }
}
