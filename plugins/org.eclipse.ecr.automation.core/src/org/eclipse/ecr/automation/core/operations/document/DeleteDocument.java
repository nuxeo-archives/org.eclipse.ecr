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
 */
package org.eclipse.ecr.automation.core.operations.document;

import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Context;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.automation.core.collectors.DocumentModelCollector;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentRef;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = DeleteDocument.ID, category = Constants.CAT_DOCUMENT, label = "Delete", description = "Delete the input document. The previous context input will be restored for the next operation.")
public class DeleteDocument {

    public static final String ID = "Document.Delete";

    @Context
    protected CoreSession session;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentRef doc) throws Exception {
        // if (soft) {
        // //TODO impl safe delete
        // throw new UnsupportedOperationException("Safe delete not yet
        // implemented");
        // }
        session.removeDocument(doc);
        // TODO ctx.pop
        return null;
    }

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
        // if (soft) {
        // //TODO impl safe delete
        // throw new UnsupportedOperationException("Safe delete not yet
        // implemented");
        // }
        session.removeDocument(doc.getRef());
        // TODO ctx.pop
        return null;
    }


}
