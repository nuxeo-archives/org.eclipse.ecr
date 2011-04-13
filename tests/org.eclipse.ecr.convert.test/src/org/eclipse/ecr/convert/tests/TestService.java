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

import java.util.List;

import org.eclipse.ecr.core.api.Blob;
import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.core.api.blobholder.SimpleBlobHolder;
import org.eclipse.ecr.core.api.impl.blob.StringBlob;
import org.eclipse.ecr.convert.api.ConversionService;
import org.eclipse.ecr.convert.api.ConverterCheckResult;
import org.eclipse.ecr.convert.api.ConverterNotAvailable;
import org.eclipse.ecr.convert.api.ConverterNotRegistered;
import org.eclipse.ecr.convert.extension.ChainedConverter;
import org.eclipse.ecr.convert.extension.Converter;
import org.eclipse.ecr.convert.extension.ConverterDescriptor;
import org.eclipse.ecr.convert.service.ConversionServiceImpl;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestService extends NXRuntimeTestCase {

    public static final String TEST_BUNDLE = "org.eclipse.ecr.convert.test";

    public static final String ANY2TEXT = "any2text";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.api");
        deployBundle("org.eclipse.ecr.convert.api");
        deployBundle("org.eclipse.ecr.convert");
    }

    public void testServiceRegistration() {
        ConversionService cs = Framework.getLocalService(ConversionService.class);
        assertNotNull(cs);
    }

    public void testServiceContrib() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib1.xml");
        ConversionService cs = Framework.getLocalService(ConversionService.class);

        Converter cv1 = ConversionServiceImpl.getConverter("dummy1");
        assertNotNull(cv1);

        ConverterDescriptor desc1 = ConversionServiceImpl.getConverterDescriptor("dummy1");
        assertNotNull(desc1);

        assertEquals("test/me", desc1.getDestinationMimeType());
        assertSame(2, desc1.getSourceMimeTypes().size());
        assertTrue(desc1.getSourceMimeTypes().contains("text/plain"));
        assertTrue(desc1.getSourceMimeTypes().contains("text/xml"));
    }

    public void testConverterLookup() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib1.xml");
        ConversionService cs = Framework.getLocalService(ConversionService.class);

        String converterName = cs.getConverterName("text/plain", "test/me");
        assertEquals("dummy1", converterName);

        converterName = cs.getConverterName("text/plain2", "test/me");
        assertNull(converterName);

        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib2.xml");

        if (true) {
            return;
        }

        converterName = cs.getConverterName("test/me", "foo/bar");
        assertEquals("dummy2", converterName);

        converterName = cs.getConverterName("text/plain", "foo/bar");
        assertEquals("dummyChain", converterName);

        Converter cv = ConversionServiceImpl.getConverter("dummyChain");
        assertNotNull(cv);
        boolean isChain = false;
        if (cv instanceof ChainedConverter) {
            ChainedConverter ccv = (ChainedConverter) cv;
            List<String> steps = ccv.getSteps();
            assertNotNull(steps);
            assertSame(2, steps.size());
            assertTrue(steps.contains("test/me"));
            assertTrue(steps.contains("foo/bar"));
            isChain = true;

        }
        assertTrue(isChain);

        converterName = cs.getConverterName("something", "somethingelse");
        assertEquals("custom", converterName);

        converterName = cs.getConverterName("any", "somethingelse");
        assertEquals("wildcard", converterName);

        converterName = cs.getConverterName("text/plain", "jacky/chan");
        assertEquals("dummyChain2", converterName);
        Converter cv2 = ConversionServiceImpl.getConverter("dummyChain2");
        assertNotNull(cv2);
        isChain = false;
        if (cv2 instanceof ChainedConverter) {
            ChainedConverter ccv = (ChainedConverter) cv2;
            List<String> steps = ccv.getSteps();
            assertNull(steps);
            isChain = true;

        }
        assertTrue(isChain);
    }

    public void testAvailability() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib2.xml");
        deployContrib(TEST_BUNDLE, "OSGI-INF/converters-test-contrib4.xml");
        ConversionService cs = Framework.getLocalService(ConversionService.class);

        ConverterCheckResult result = null;

        // ** not existing converter
        // check registration check
        boolean notRegistred = false;

        try {
            result = cs.isConverterAvailable("toto");
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        }
        assertTrue(notRegistred);

        // check call
        notRegistred = false;
        try {
            cs.convert("toto", new SimpleBlobHolder(new StringBlob("")), null);
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        }
        assertTrue(notRegistred);

        // not available converter

        notRegistred = false;
        try {
            result = cs.isConverterAvailable("NotAvailableConverter");
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        }
        assertFalse(notRegistred);
        assertFalse(result.isAvailable());
        assertNotNull(result.getErrorMessage());
        assertNotNull(result.getInstallationMessage());

        notRegistred = false;
        boolean notAvailable = false;
        try {
            cs.convert("NotAvailableConverter", new SimpleBlobHolder(
                    new StringBlob("")), null);
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        } catch (ConverterNotAvailable e) {
            notAvailable = true;
        }
        assertFalse(notRegistred);
        assertTrue(notAvailable);

        // ** available converter
        notRegistred = false;
        notAvailable = false;
        try {
            result = cs.isConverterAvailable("dummy2");
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        }
        assertFalse(notRegistred);
        assertTrue(result.isAvailable());
        assertNull(result.getErrorMessage());
        assertNull(result.getInstallationMessage());
        assertSame(2, result.getSupportedInputMimeTypes().size());

        notRegistred = false;
        try {
            cs.convert("dummy2", new SimpleBlobHolder(new StringBlob("")), null);
        } catch (ConverterNotRegistered e) {
            notRegistred = true;
        } catch (ConverterNotAvailable e) {
            notAvailable = true;
        }
        assertFalse(notRegistred);
        assertFalse(notAvailable);
    }

    public void testServiceConfig() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/convert-service-config-test.xml");
        ConversionService cs = Framework.getLocalService(ConversionService.class);

        assertEquals(12, ConversionServiceImpl.getGCIntervalInMinutes());
        assertEquals(132, ConversionServiceImpl.getMaxCacheSizeInKB());
        assertFalse(ConversionServiceImpl.isCacheEnabled());
    }

    public void testAny2TextConverter() throws Exception {
        ConversionService cs = Framework.getLocalService(ConversionService.class);
        String expected = "abc def";
        BlobHolder bh = new SimpleBlobHolder(new StringBlob(expected, "text/plain", "UTF-8"));
        BlobHolder res = cs.convert(ANY2TEXT, bh, null);
        Blob blob = res.getBlob();
        String string = new String(blob.getByteArray(), "UTF-8");
        assertEquals(expected, string);
    }

}
