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

import java.io.Serializable;

import org.eclipse.ecr.automation.OperationException;
import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Context;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.automation.core.annotations.Param;
import org.eclipse.ecr.automation.core.collectors.DocumentModelCollector;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.model.Property;
import org.eclipse.ecr.core.schema.types.SimpleType;
import org.eclipse.ecr.core.schema.types.Type;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = SetDocumentProperty.ID, category = Constants.CAT_DOCUMENT, label = "Update Property", description = "Set a single property value on the input document. The property is specified using its xpath. The document is automatically saved if 'save' parameter is true. If you unset the 'save' you need to save it later using Save Document operation. Return the modified document.")
public class SetDocumentProperty {

    public static final String ID = "Document.SetProperty";

    @Context
    protected CoreSession session;

    @Param(name = "xpath")
    protected String xpath;

    @Param(name = "value")
    protected Serializable value;

    @Param(name = "save", required = false, values = "true")
    protected boolean save = true;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
        Property p = doc.getProperty(xpath);
        Type type = p.getField().getType();
        if (!type.isSimpleType()) {
            throw new OperationException(
                    "Only scalar types can be set using update operation");
        }
        if (value.getClass() == String.class) {
            p.setValue(((SimpleType) type).getPrimitiveType().decode(
                    (String) value));
        } else {
            p.setValue(value);
        }
        if (save) {
            doc = session.saveDocument(doc);
        }

        return doc;
    }

}
