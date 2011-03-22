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
package org.eclipse.ecr.automation.core.operations.stack;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.automation.OperationException;
import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Context;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.api.DocumentRefList;
import org.eclipse.ecr.core.api.impl.DocumentModelListImpl;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = PullDocumentList.ID, category = Constants.CAT_EXECUTION_STACK,
        label = "Pull Document List", description = "Restore the first saved input document list in the context input stack")
public class PullDocumentList {

    public static final String ID = "Document.PullList";

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModelList run() throws Exception {
        Object obj = ctx.pull(Constants.O_DOCUMENTS);
        if (obj instanceof DocumentModelList) {
            return (DocumentModelList) obj;
        } else if (obj instanceof DocumentRefList) {
            CoreSession session = ctx.getCoreSession();
            DocumentRefList refs = (DocumentRefList) obj;
            DocumentModelListImpl list = new DocumentModelListImpl(
                    (int) refs.totalSize());
            for (DocumentRef ref : refs) {
                list.add(session.getDocument(ref));
            }
            // FIXME: variable list is never used!
        }
        throw new OperationException(
                "Illegal state error for pull document operation. The context stack doesn't contains a document list on its bottom");
    }

}
