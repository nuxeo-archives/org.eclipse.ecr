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
 * $Id$
 */

package org.eclipse.ecr.core.io.impl.plugins;

import java.util.Map;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.io.DocumentReader;
import org.eclipse.ecr.core.io.DocumentReaderFactory;

public class DocumentTreeReaderFactory implements DocumentReaderFactory {

    @Override
    public DocumentReader createDocReader(Map<String, Object> params) throws ClientException {
        CoreSession session = null;
        DocumentRef root = null;
        return new DocumentTreeReader(session, root);
    }

}
