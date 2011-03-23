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
import org.eclipse.ecr.automation.core.util.Properties;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = UpdateDocument.ID, category = Constants.CAT_DOCUMENT, label = "Update Properties", description = "Set multiple properties on the input document. The properties are specified as <i>key=value</i> pairs separated by a new line. The key used for a property is the property xpath. To specify multi-line values you can use a \\ charcater followed by a new line. <p>Example:<pre>dc:title=The Document Title<br>dc:description=foo bar</pre>. Returns back the updated document.")
public class UpdateDocument {

    public static final String ID = "Document.Update";

    @Context
    protected CoreSession session;

    @Param(name = "properties")
    protected Properties properties;

    @Param(name = "save", required = false, values = "true")
    protected boolean save = true;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
        DocumentHelper.setProperties(session, doc, properties);
        if (save) {
            doc = session.saveDocument(doc);
        }
        return doc;
    }

}
