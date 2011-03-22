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
import org.eclipse.ecr.automation.core.util.DocumentHelper;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.VersioningOption;
import org.eclipse.ecr.core.versioning.VersioningService;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = CreateVersion.ID, category = Constants.CAT_DOCUMENT, label = "Snapshot Version", description = "Create a new version for the input document. Any modification made on the document by the chain will be automatically saved. Increment version if this was specified through the 'snapshot' parameter. Returns the live document (not the version).")
public class CreateVersion {

    public static final String ID = "Document.CreateVersion";

    @Context
    protected CoreSession session;

    @Param(name = "increment", required = false, widget = Constants.W_OPTION, values = {
            "None", "Minor", "Major" })
    protected String snapshot = "None";

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
        VersioningOption vo;
        if ("Minor".equals(snapshot)) {
            vo = VersioningOption.MINOR;
        } else if ("Major".equals(snapshot)) {
            vo = VersioningOption.MAJOR;
        } else {
            vo = null;
        }
        if (vo != null) {
            doc.putContextData(VersioningService.VERSIONING_OPTION, vo);
        }
        // return session.saveDocument(doc);
        return DocumentHelper.saveDocument(session, doc);
    }

}
