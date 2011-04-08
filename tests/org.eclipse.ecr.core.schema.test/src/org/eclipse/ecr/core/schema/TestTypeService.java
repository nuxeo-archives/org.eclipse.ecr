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
 * $Id: TestTypeService.java 26932 2007-11-07 15:05:49Z gracinet $
 */

package org.eclipse.ecr.core.schema;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author <a href="mailto:sf@nuxeo.com">Stefane Fermigier</a>
 */
public class TestTypeService extends NXRuntimeTestCase {

    private TypeService ts;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.schema");
        ts = (TypeService) Framework.getRuntime().getComponent(
                TypeService.NAME);
    }

    public void testTypeService() {
        assertNull(ts.getConfiguration());
        assertNotNull(ts.getSchemaLoader());
        assertNotNull(ts.getTypeManager());
    }

}
