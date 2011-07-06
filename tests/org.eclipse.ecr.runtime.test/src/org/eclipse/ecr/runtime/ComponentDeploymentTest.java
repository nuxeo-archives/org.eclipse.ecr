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

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.ComponentInstance;
import org.eclipse.ecr.runtime.model.ComponentManager;
import org.eclipse.ecr.runtime.model.ComponentName;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ComponentDeploymentTest extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp1.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp2.xml");
    }

    public void testContributions() {
        RuntimeService runtime = Framework.getRuntime();
        ComponentManager mgr = runtime.getComponentManager();
        assertTrue(mgr.size() > 0);

        ComponentInstance co = runtime.getComponentInstance("service:my.comp1");
        assertNotNull(co);
        assertEquals(co.getName(), new ComponentName("service:my.comp1"));

        co = runtime.getComponentInstance("service:my.comp2");
        assertNotNull(co);
        assertEquals(co.getName(), new ComponentName("service:my.comp2"));

        mgr.unregister(new ComponentName("service:my.comp2"));
        co = runtime.getComponentInstance("service:my.comp2");
        assertNull(co);
        co = runtime.getComponentInstance("service:my.comp1");
        assertNotNull(co);
    }


}
