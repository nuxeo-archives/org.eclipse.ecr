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
 * $Id: DocumentChildrenReader.java 29029 2008-01-14 18:38:14Z ldoguin $
 */

package org.eclipse.ecr.core.io.impl.plugins;

import java.io.IOException;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentModelIterator;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.io.ExportedDocument;
import org.eclipse.ecr.core.io.impl.ExportedDocumentImpl;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DocumentChildrenReader extends DocumentModelReader {

    private DocumentModelIterator iterator;

    public DocumentChildrenReader(CoreSession session, DocumentModel root)
            throws ClientException {
        super(session);
        iterator = session.getChildrenIterator(root.getRef());
    }

    public DocumentChildrenReader(CoreSession session, DocumentRef root)
            throws ClientException {
        this(session, session.getDocument(root));
    }

    @Override
    public void close() {
        super.close();
        iterator = null;
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (iterator.hasNext()) {
            DocumentModel docModel = iterator.next();
            return new ExportedDocumentImpl(docModel);
        }
        return null;
    }

}
