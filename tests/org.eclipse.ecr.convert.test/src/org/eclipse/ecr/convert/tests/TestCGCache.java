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

public class TestCGCache extends NXRuntimeTestCase {

    public static final String TEST_BUNDLE = "org.eclipse.ecr.convert.test";

    ConversionService cs;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.api");
        deployBundle("org.eclipse.ecr.convert.api");
        deployBundle("org.eclipse.ecr.convert");
        deployContrib(TEST_BUNDLE,
                "OSGI-INF/convert-service-config-enabled-gc.xml");
        cs = Framework.getLocalService(ConversionService.class);
    }

    public void testCGTask() throws Exception {
        int noRuns = ConversionCacheGCManager.getGCRuns();
        Converter cv = deployConverter();
        assertNotNull(cv);

        int cacheSize1 = ConversionCacheHolder.getNbCacheEntries();
        BlobHolder bh = getBlobHolder();
        BlobHolder result = cs.convert("identity", bh, null);
        assertNotNull(result);

        int cacheSize2 = ConversionCacheHolder.getNbCacheEntries();
        // check new cache entry was created
        assertEquals(1, cacheSize2 - cacheSize1);

        // fire the frameworkStarted event and make sure that the GC
        // has run and that the cache is empty
        fireFrameworkStarted();
        // wait for the CHTread to run
        int retryCount = 0;
        while (ConversionCacheGCManager.getGCRuns() == noRuns
                && retryCount++ < 5) {
            Thread.sleep(1000);
        }
        assertTrue(ConversionCacheGCManager.getGCRuns() > 0);

        int cacheSize3 = ConversionCacheHolder.getNbCacheEntries();
        assertEquals(0, cacheSize3);
    }

    private Converter deployConverter() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib3.xml");
        return ConversionServiceImpl.getConverter("identity");
    }

    private BlobHolder getBlobHolder() throws Exception {
        URL url = lookupBundle(TEST_BUNDLE).getEntry("resources/test-data/hello.doc");
        File file = new File(url.getPath());
        assertTrue(file.length() > 0);
        Blob blob = new FileBlob(file);
        blob.setFilename("hello.doc");
        blob.setMimeType("application/msword");
        return new SimpleBlobHolder(blob);
    }

}
