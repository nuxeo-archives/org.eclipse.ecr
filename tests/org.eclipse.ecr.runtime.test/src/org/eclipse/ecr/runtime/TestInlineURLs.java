/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephane Lacoin
 */
package org.eclipse.ecr.runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.ecr.testlib.protocols.inline.InlineURLFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;

/**
 *
 * @author matic
 *
 */
public class TestInlineURLs {

    String info = "some info";
    URL inlineURL;

    @BeforeClass public static void installHandler() throws Exception {
        InlineURLFactory.install();
    }

    @Before public void encodeURL() throws IOException {
        inlineURL = InlineURLFactory.newURL(info);
    }

    @Test public void hasCorrectContent() throws IOException {
        String inlinedContent = InlineURLFactory.newObject(String.class, inlineURL);
        assertThat(inlinedContent, equalTo(info));
    }

    @Test public void canRead() throws IOException {
        InputStream stream = inlineURL.openStream();
        String inlinedContent = FileUtils.read(stream);
        assertThat(inlinedContent, equalTo(info));
    }
}
