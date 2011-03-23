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
package org.eclipse.ecr.automation.core.operations.blob;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Context;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.automation.core.annotations.Param;
import org.eclipse.ecr.automation.core.collectors.DocumentModelCollector;
import org.eclipse.ecr.core.api.Blob;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.model.Property;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = SetBlobFileName.ID, category = Constants.CAT_BLOB, label = "Set File Name", description = "Modify the filename of a file stored in the input document. The file is found in the input document given its xpath specified through the 'xpath' parameter. Return back the input document.")
public class SetBlobFileName {

    public static final String ID = "Blob.SetFilename";

    @Context
    protected OperationContext ctx;

    @Context
    protected CoreSession session;

    @Param(name = "name")
    protected String name;

    @Param(name = "xpath", required=false, values="file:content")
    protected String xpath = "file:content";

    @Param(name = "save", required = false, values="true")
    protected boolean save = true;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws java.lang.Exception {
        Property p = doc.getProperty(xpath);
        Object o = p.getValue();
        if (o instanceof Blob) {
            Blob blob = (Blob)o;
            blob.setFilename(name);
            p.setValue(blob);
        }
        if (save) {
            doc = session.saveDocument(doc);
        }
        return doc;
    }

}
