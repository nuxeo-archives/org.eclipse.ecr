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
import org.eclipse.ecr.automation.core.collectors.DocumentModelListCollector;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.impl.DocumentModelListImpl;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = MultiPublishDocument.ID, category = Constants.CAT_DOCUMENT, label = "Multi-Publish", description = "Publish the input document(s) into several target sections. The target is evaluated to a document list (can be a path, UID or EL expression). Existing proxy is overrided if the override attribute is set. Returns a list with the created proxies.")
public class MultiPublishDocument {

    public static final String ID = "Document.MultiPublish";

    @Context
    protected CoreSession session;

    @Param(name = "target")
    protected DocumentModelList target;

    @Param(name = "override", required = false, values = "true")
    protected boolean override = true;

    @OperationMethod(collector=DocumentModelListCollector.class)
    public DocumentModelList run(DocumentModel doc) throws Exception {
        DocumentModelListImpl docs = new DocumentModelListImpl();
        for (DocumentModel t : target) {
            docs.add(session.publishDocument(doc, t, override));
        }
        return docs;
    }

}
