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
 */

package org.eclipse.ecr.convert.tests;

import java.io.File;
import java.net.URL;

import org.nuxeo.common.utils.FileUtils;
import org.eclipse.ecr.core.api.Blob;
import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.core.api.blobholder.SimpleBlobHolder;
import org.eclipse.ecr.core.api.impl.blob.FileBlob;
import org.eclipse.ecr.convert.api.ConversionService;
import org.eclipse.ecr.convert.cache.ConversionCacheGCManager;
import org.eclipse.ecr.convert.cache.ConversionCacheHolder;
import org.eclipse.ecr.convert.extension.Converter;
import org.eclipse.ecr.convert.service.ConversionServiceImpl;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestCache extends NXRuntimeTestCase {

    public static final String TEST_BUNDLE = "org.eclipse.ecr.convert.test";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.api");
        deployBundle("org.eclipse.ecr.convert.api");
        deployBundle("org.eclipse.ecr.convert");
        deployContrib(TEST_BUNDLE,
                "OSGI-INF/convert-service-config-enabled.xml");
    }

    public void testCache() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib3.xml");
        ConversionService cs = Framework.getLocalService(ConversionService.class);

        Converter cv = ConversionServiceImpl.getConverter("identity");
        assertNotNull(cv);

        int cacheSize1 = ConversionCacheHolder.getNbCacheEntries();
        long cacheHits1 = ConversionCacheHolder.getCacheHits();

        URL url = lookupBundle(TEST_BUNDLE).getEntry("resources/test-data/hello.doc");
        File file = new File(url.getPath());
        assertTrue(file.length() > 0);

        Blob blob = new FileBlob(file);
        blob.setFilename("hello.doc");
        blob.setMimeType("application/msword");

        BlobHolder bh = new SimpleBlobHolder(blob);

        BlobHolder result = cs.convert("identity", bh, null);

        assertNotNull(result);

        int cacheSize2 = ConversionCacheHolder.getNbCacheEntries();

        // check new cache entry was created
        assertEquals(1, cacheSize2 - cacheSize1);

        BlobHolder result2 = cs.convert("identity", bh, null);

        // check NO new cache entry was created
        cacheSize2 = ConversionCacheHolder.getNbCacheEntries();
        assertEquals(1, cacheSize2 - cacheSize1);

        long cacheHits2 = ConversionCacheHolder.getCacheHits();

        // check cache hits
        assertEquals(1, cacheHits2 - cacheHits1);

        // force GC
        ConversionCacheGCManager.doGC(file.length() / 1024);

        int cacheSize3 = ConversionCacheHolder.getNbCacheEntries();
        assertEquals(0, cacheSize3);
    }

}
