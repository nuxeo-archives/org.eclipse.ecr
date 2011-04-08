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
 */

package org.eclipse.ecr.core.api.impl.blob;

import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.impl.DocumentModelImpl;
import org.eclipse.ecr.core.schema.SchemaManager;
import org.eclipse.ecr.core.schema.SchemaManagerImpl;
import org.eclipse.ecr.core.schema.TypeService;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestMediaType extends NXRuntimeTestCase{

    public static final String SCHEMA_NAME = "media";
    public static final String SCHEMA_PREFIX = "media";


    protected SchemaManager typeMgr;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.schema");
        deployContrib("org.eclipse.ecr.core.api.test", "OSGI-INF/test-media-types-contrib.xml");
        typeMgr = getTypeManager();
    }

     public static SchemaManagerImpl getTypeManager() {
            return (SchemaManagerImpl) getTypeService().getTypeManager();
     }

     public static TypeService getTypeService() {
            return (TypeService) Framework.getRuntime().getComponent(TypeService.NAME);
     }

    // shema name != prefix name
    public void testDifferentPrefix()  throws Exception {
        DocumentModel doc = new DocumentModelImpl("/", "mediaDoc", "Media");
        doc.setPropertyValue("media:title", "Media Title");
        Object o1 = doc.getProperty("media:title");
        Object o2 = doc.getProperty("media:/title");
        assertEquals(o1, o2);

        // using the prefix
        o2 = doc.getProperty("m:title");
        assertEquals(o1, o2);

        o2 = doc.getProperty("m:/title");
        assertEquals(o1, o2);
    }

    // shema name = prefix name
    public void testSamePrefix()  throws Exception {
        DocumentModel doc = new DocumentModelImpl("/", "mediaDoc", "SameMedia");
        doc.setPropertyValue("sameMedia:title", "Media Title");
        Object o1 = doc.getProperty("sameMedia:title");
        Object o2 = doc.getProperty("sameMedia:/title");
        assertEquals(o1, o2);
    }

}
