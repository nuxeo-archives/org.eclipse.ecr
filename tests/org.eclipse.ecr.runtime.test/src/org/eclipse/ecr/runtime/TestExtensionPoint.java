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
 *
 * $Id$
 */

package org.eclipse.ecr.runtime;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/** @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a> */
public class TestExtensionPoint extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.eclipse.ecr.runtime.test", "resources/BaseXPoint.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "resources/OverridingXPoint.xml");
    }

    public void testOverride() {
        ComponentWithXPoint co = (ComponentWithXPoint) Framework.getRuntime().getComponent(
                ComponentWithXPoint.NAME);
        DummyContribution[] contribs = co.getContributions();
        assertEquals(2, contribs.length);
        assertSame(contribs[0].getClass(), DummyContribution.class);
        assertSame(contribs[1].getClass(), DummyContributionOverriden.class);
        assertEquals("XP contrib", contribs[0].message);
        assertEquals("OverXP contrib", contribs[1].message);
        assertEquals("My duty is to override", ((DummyContributionOverriden) contribs[1]).name);
    }

}
