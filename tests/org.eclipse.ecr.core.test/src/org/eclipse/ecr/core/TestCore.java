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

package org.eclipse.ecr.core;

import org.eclipse.ecr.core.model.NoSuchRepositoryException;
import org.eclipse.ecr.core.model.Repository;
import org.eclipse.ecr.core.schema.DocumentType;
import org.eclipse.ecr.core.schema.NXSchema;
import org.eclipse.ecr.core.schema.SchemaManager;
import org.eclipse.ecr.core.schema.TypeConstants;
import org.eclipse.ecr.core.schema.types.Schema;
import org.eclipse.ecr.core.schema.types.Type;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestCore extends NXRuntimeTestCase {

    private SchemaManager typeManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle(CoreUTConstants.SCHEMA_BUNDLE);
        deployContrib(CoreUTConstants.CORE_BUNDLE, "OSGI-INF/CoreService.xml");
        deployContrib(CoreUTConstants.CORE_BUNDLE, "OSGI-INF/RepositoryService.xml");
        deployContrib(CoreUTConstants.CORE_BUNDLE, "OSGI-INF/CoreExtensions.xml");
        typeManager = NXSchema.getSchemaManager();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        typeManager = null;
    }

    public void testRepositoryManager() {
        try {
            Repository repo = NXCore.getRepository("improbable name");
            assertNull(repo);
        } catch (NoSuchRepositoryException e) {
            // do nothing
        }
    }

    // :XXX: You can't test that since other tests are registering or deleting
    // document types from here.
    public void xxxtestDocTypes() {
        assertEquals(3, typeManager.getDocumentTypesCount());

        // TODO: Iterate on docTypes and check things out

        DocumentType docType = typeManager.getDocumentType(TypeConstants.DOCUMENT);
        assertNotNull(docType);
        docType = typeManager.getDocumentType("other name");
        assertNull(docType);
    }

    // :XXX: You can't test that since other tests are registering or deleting
    // document types from here.
    public void xxxtestSchemaRegistry() {
        Schema[] schemas = typeManager.getSchemas();
        // Default schemas registry is empty
        assertEquals(0, schemas.length);
    }

    // :XXX: You can't test that since other tests are registering or deleting
    // document types from here.
    public void xxxtestTypeRegistry() {
        Type[] types = typeManager.getTypes();
        // Default types registry has 2 types
        // TODO: what is this type and what should we test?
        // This test needs to be fixed.
        assertEquals(2, types.length);
    }

}
