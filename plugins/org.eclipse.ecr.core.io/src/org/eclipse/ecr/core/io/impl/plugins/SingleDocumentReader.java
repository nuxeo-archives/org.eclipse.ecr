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
 * $Id: SingleDocumentReader.java 30256 2008-02-18 21:52:11Z tdelprat $
 */

package org.eclipse.ecr.core.io.impl.plugins;

import java.io.IOException;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.io.ExportedDocument;
import org.eclipse.ecr.core.io.impl.ExportedDocumentImpl;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SingleDocumentReader extends DocumentModelReader {

    private DocumentModel doc;

    private boolean enableRepeatedReads = false;

    private boolean readDone = false;

    public SingleDocumentReader(CoreSession session, DocumentModel root) {
        super(session);
        doc = root;
    }

    public SingleDocumentReader(CoreSession session, DocumentRef root)
            throws ClientException {
        this(session, session.getDocument(root));
    }

    @Override
    public void close() {
        super.close();
        session = null;
        doc = null;
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (doc != null) {
            if (readDone && !enableRepeatedReads) {
                return null;
            } else {
                readDone = true;
                return new ExportedDocumentImpl(doc);
            }
        }
        doc = null;
        return null;
    }

    public void setEnableRepeatedReads(boolean enableRepeatedReads) {
        this.enableRepeatedReads = enableRepeatedReads;
    }

}
