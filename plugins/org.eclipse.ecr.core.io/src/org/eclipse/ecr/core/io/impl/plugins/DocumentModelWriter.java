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
 * $Id: DocumentModelWriter.java 30413 2008-02-21 18:38:54Z sfermigier $
 */

package org.eclipse.ecr.core.io.impl.plugins;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentLocation;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.PathRef;
import org.eclipse.ecr.core.io.DocumentTranslationMap;
import org.eclipse.ecr.core.io.ExportedDocument;
import org.eclipse.ecr.core.io.impl.AbstractDocumentModelWriter;
import org.eclipse.ecr.core.io.impl.DocumentTranslationMapImpl;
import org.nuxeo.common.utils.Path;

/**
 * A writer which is creating new docs or updating existing docs.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
// TODO: improve it ->
// modify core session to add a batch create method and use it
public class DocumentModelWriter extends AbstractDocumentModelWriter {

    private static final Log log = LogFactory.getLog(DocumentModelWriter.class);

    /**
     * @param session the session to the repository where to write
     * @param parentPath where to write the tree. this document will be used as
     *            the parent of all top level documents passed as input. Note
     *            that you may have
     */
    public DocumentModelWriter(CoreSession session, String parentPath) {
        super(session, parentPath);
    }

    public DocumentModelWriter(CoreSession session, String parentPath,
            int saveInterval) {
        super(session, parentPath, saveInterval);
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument xdoc) throws IOException {
        if (xdoc.getDocument() == null) {
            // not a valid doc -> this may be a regular folder for example the
            // root of the tree
            return null;
        }
        Path path = xdoc.getPath();
//        if (path.isEmpty() || path.isRoot()) {
//            return; // TODO avoid to import the root
//        }
        path = root.append(path); // compute target path

        try {
            return doWrite(xdoc, path);
        } catch (ClientException e) {
            IOException ioe = new IOException(
                    "Failed to import document in repository: "
                            + e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            log.error(e, e);
            return null;
        }
    }

    private DocumentTranslationMap doWrite(ExportedDocument xdoc,
            Path targetPath) throws ClientException {

        DocumentModel previousDoc = null;
        /*NXP-1688 Rux: if the document doesn't exist, the thrown ClientException is
         * wrapped. Instead, an explicit query about the existence should do the job.
         */
        PathRef pathRef = new PathRef(targetPath.toString());
        try {
            if (session.exists(pathRef)) {
                previousDoc = session.getDocument(pathRef);
            }
        } catch (ClientException ce) {
            //don't care, document considered inexistent
            previousDoc = null;
        }

        DocumentModel doc;
        if (previousDoc == null) {
            doc = createDocument(xdoc, targetPath);
        } else {
            doc = updateDocument(xdoc, previousDoc);
        }

        DocumentLocation source = xdoc.getSourceLocation();
        DocumentTranslationMap map = new DocumentTranslationMapImpl(
                source.getServerName(), doc.getRepositoryName());
        map.put(source.getDocRef(), doc.getRef());
        return map;
    }

}
