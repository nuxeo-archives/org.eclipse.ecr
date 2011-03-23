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
import org.eclipse.ecr.automation.core.annotations.Param;
import org.eclipse.ecr.automation.core.collectors.DocumentModelCollector;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentRef;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = MoveDocument.ID, category = Constants.CAT_DOCUMENT, label = "Move", description = "Move the input document into the target folder.")
public class MoveDocument {

    public static final String ID = "Document.Move";

    @Context
    protected CoreSession session;

    @Param(name = "target")
    protected DocumentRef target; // the path or the ID

    @Param(name = "name", required = false)
    protected String name;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
        String n = name;
        if (name == null || name.length() == 0) {
            n = doc.getName();
        }
        return session.move(doc.getRef(), target, n);
    }

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentRef ref) throws Exception {
        String n = name;
        if (name == null || name.length() == 0) {
            n = session.getDocument(ref).getName();
        }
        return session.move(ref, target, n);
    }

}
