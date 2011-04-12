/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.eclipse.ecr.core.storage.sql;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.FieldDescriptor;
import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.FulltextIndexDescriptor;
import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.ServerDescriptor;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;
import org.nuxeo.common.xmap.XMap;

public class TestRepositoryDescriptor extends NXRuntimeTestCase {

    public static final String TEST_BUNDLE = "org.eclipse.ecr.core.storage.sql.test";

    protected RepositoryDescriptor desc;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        XMap xmap = new XMap();
        xmap.register(RepositoryDescriptor.class);
        xmap.register(FulltextIndexDescriptor.class);
        URL url = lookupBundle(TEST_BUNDLE).getEntry(
                "OSGI-INF/test-repository-descriptor.xml");
        desc = (RepositoryDescriptor) xmap.load(url);
    }

    public void testBasic() throws Exception {
        assertEquals("foo", desc.name);
        assertTrue(desc.clusteringEnabled);
        assertEquals(1234, desc.clusteringDelay);
    }

    @SuppressWarnings("unchecked")
    public void testFulltext() throws Exception {
        assertEquals("french", desc.fulltextAnalyzer);
        assertEquals("nuxeo", desc.fulltextCatalog);
        assertNotNull(desc.fulltextIndexes);
        assertEquals(4, desc.fulltextIndexes.size());
        FulltextIndexDescriptor fti;

        fti = desc.fulltextIndexes.get(0);
        assertNull(fti.name);
        assertNull(fti.fieldType);
        assertEquals(new HashSet(), fti.fields);
        assertEquals(new HashSet(Collections.singleton("dc:creator")),
                fti.excludeFields);

        fti = desc.fulltextIndexes.get(1);
        assertEquals("titraille", fti.name);
        assertNull(fti.fieldType);
        assertEquals(new HashSet(Arrays.asList("dc:title", "dc:description")),
                fti.fields);
        assertEquals(new HashSet(), fti.excludeFields);

        fti = desc.fulltextIndexes.get(2);
        assertEquals("blobs", fti.name);
        assertEquals("blob", fti.fieldType);
        assertEquals(new HashSet(), fti.fields);
        assertEquals(new HashSet(Collections.singleton("foo:bar")),
                fti.excludeFields);

        fti = desc.fulltextIndexes.get(3);
        assertEquals("pictures", fti.name);
        assertNull(fti.fieldType);
        assertEquals(
                new HashSet(Collections.singleton("picture:views/*/filename")),
                fti.fields);
        assertEquals(new HashSet(), fti.excludeFields);
        assertEquals("martian", fti.analyzer);
        assertEquals("other", fti.catalog);
    }

    public void testLargeTextFields() throws Exception {
        assertNotNull(desc.schemaFields);
        assertEquals(2, desc.schemaFields.size());
        FieldDescriptor fd;
        fd = desc.schemaFields.get(0);
        assertEquals("my:bignote", fd.field);
        assertEquals("biig", fd.type);
        fd = desc.schemaFields.get(1);
        assertEquals("foo", fd.field);
        assertEquals("xyz", fd.type);
    }

    public void testBinaryStorePath() throws Exception {
        assertEquals("/foo/bar", desc.binaryStorePath);
    }

    public void testListenConnect() throws Exception {
        ServerDescriptor s;
        s = desc.listen;
        assertEquals("localhost0", s.host);
        assertEquals(81810, s.port);
        assertEquals("/nuxeo0", s.path);

        assertNotNull(desc.connect);
        assertEquals(2, desc.connect.size());
        s = desc.connect.get(0);
        assertEquals("localhost1", s.host);
        assertEquals(81811, s.port);
        assertEquals("/nuxeo1", s.path);
        s = desc.connect.get(1);
        assertEquals("localhost2", s.host);
        assertEquals(81812, s.port);
        assertEquals("/nuxeo2", s.path);
    }

}
