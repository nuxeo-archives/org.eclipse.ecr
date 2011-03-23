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

package org.eclipse.ecr.core.api.repository;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.DocumentRef;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
// TODO: never used. Remove?
@Deprecated
public class DocumentQuery {

    public DocumentModelList query(String query) {
        return null;
    }

    public DocumentModelList getChildren(DocumentRef parent) {
        return null;
    }

    public DocumentModelList getFiles(DocumentRef parent) throws ClientException {
        return null;
    }

    public DocumentModelList getFolders(DocumentRef parent) throws ClientException {
        return null;
    }

}
