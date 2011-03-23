/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dragos Mihalache
 *     Florent Guillaume
 */
package org.eclipse.ecr.core.versioning;

import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.adapter.DocumentAdapterFactory;

/**
 * Adapter class factory for Versioning Document interface.
 */
public class VersioningDocumentAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        return new VersioningDocumentAdapter(doc);
    }

}
