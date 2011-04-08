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
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.eclipse.ecr.core.api;

import java.util.Collections;

import org.nuxeo.common.utils.Path;
import org.eclipse.ecr.core.api.impl.DocumentModelImpl;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestDocumentModel extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.schema");
    }

    @SuppressWarnings({"ObjectEqualsNull", "SimplifiableJUnitAssertion"})
    public void testDocumentModelImpl() throws Exception {
        DocumentModel model = new DocumentModelImpl("my type");

        assertEquals("my type", model.getType());

        // assertNull(model.getACP());
        // assertNull(model.getAdapter(Object.class));

        assertNull(model.getDataModel("toto"));
        assertTrue(model.getDataModels().isEmpty());
        assertTrue(model.getDataModelsCollection().isEmpty());

        assertEquals(Collections.emptySet(), model.getDeclaredFacets());
        assertEquals(0, model.getDeclaredSchemas().length);
        assertEquals(Collections.emptySet(), model.getFacets());
        assertEquals(0, model.getSchemas().length);
        assertNull(model.getId());
        assertNull(model.getLock()); // old
        assertNull(model.getLockInfo());
        assertNull(model.getName());
        assertNull(model.getParentRef());
        assertNull(model.getPath());
        assertNull(model.getPathAsString());
        assertNull(model.getProperties(""));
        assertNull(model.getProperty("", ""));
        assertNull(model.getRef());
        assertNull(model.getSessionId());

        assertFalse(model.hasFacet(""));
        assertFalse(model.hasSchema(""));
        assertFalse(model.isDownloadable());
        assertFalse(model.isFolder());
        assertFalse(model.isLocked());
        assertFalse(model.isVersionable());
        assertFalse(model.isVersion());
        assertNull(model.getRepositoryName());
        assertNull(model.getSessionId());
        //assertNull(model.getLifeCyclePolicy());

        assertTrue(model.equals(model));
        assertFalse(model.equals(null));

        assertNotNull(model.toString());
    }

}
