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
package org.eclipse.ecr.runtime.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.ecr.runtime.model.StreamRef;
import org.eclipse.ecr.testlib.protocols.inline.InlineURLFactory;

public class InlineRef implements StreamRef {

    protected final String id;
    protected final String content;

    public InlineRef(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public URL asURL() {
        try {
            return InlineURLFactory.newURL(content);
        } catch (Exception e) {
            throw new Error("Cannot encode inline:... URL", e);
        }
    }

}
