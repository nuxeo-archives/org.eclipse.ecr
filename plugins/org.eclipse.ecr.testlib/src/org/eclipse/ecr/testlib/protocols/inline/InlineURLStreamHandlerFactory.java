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

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class InlineURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("inline".equals(protocol)) {
            return new InlineURLStreamHandler();
        }
        return null;
    }

}
