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
package org.eclipse.ecr.testlib.protocols.inline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class InlineURLConnection extends URLConnection {
;
    protected final Object content;

    protected InlineURLConnection(URL url)  {
        super(url);
        try {
            content = InlineURLFactory.newObject(Object.class, url);
        } catch (IOException e) {
            throw new Error("Cannot decode data from " + url, e);
        }
    }

    @Override
    public void connect() throws IOException {
        ;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.toString().getBytes());
    }

}
