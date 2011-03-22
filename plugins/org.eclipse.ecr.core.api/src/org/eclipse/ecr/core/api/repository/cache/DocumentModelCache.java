/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id$
 */

package org.eclipse.ecr.core.api.repository.cache;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.DocumentRef;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 *
 */
public interface DocumentModelCache {

    DocumentModel cacheDocument(DocumentModel doc);

    DocumentModel uncacheDocument(DocumentRef ref);

    DocumentModel getCachedDocument(DocumentRef ref);

    void flushDocumentCache();

    DocumentModel fetchDocument(DocumentRef ref) throws ClientException;

    void cacheChildren(DocumentRef parent, DocumentModelList children);

    void uncacheChildren(DocumentRef parent);

    DocumentModelList  fetchChildren(DocumentRef parent) throws Exception;

    DocumentModelList  getCachedChildren(DocumentRef parent) throws ClientException;

    void cacheChild(DocumentRef parent, DocumentRef child);

    void uncacheChild(DocumentRef parent, DocumentRef child);

    DocumentModelList fetchAndCacheChildren(DocumentRef parent) throws ClientException;

    void handleDirtyUpdateTag(Object tag);

    void addListener(DocumentModelCacheListener listener);

    void removeListener(DocumentModelCacheListener listener);

}
