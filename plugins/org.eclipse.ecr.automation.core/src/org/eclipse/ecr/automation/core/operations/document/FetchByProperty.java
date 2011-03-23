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
import org.eclipse.ecr.automation.core.util.StringList;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.impl.DocumentModelListImpl;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = FetchByProperty.ID, category = Constants.CAT_FETCH, label = "Fetch By Property", description = "For each specified string property value, fetch all documents that match the property and the optional where clause. Matching documents are collected into a list and the returned to the next operation. The operation has no input.")
public class FetchByProperty {

    public static final String ID = "Document.FetchByProperty";

    @Context
    protected CoreSession session;

    @Param(name = "property", required = true)
    protected String property;

    @Param(name = "values", required = true)
    protected StringList values;

    @Param(name = "query", required = false)
    protected String query;

    @OperationMethod
    public DocumentModelList run() throws Exception {
        // TODO use SQLQueryParser.prepareStringLiteral to escape the string
        // values.
        if (query != null && query.trim().length() == 0) {
            query = null;
        }
        String queryWhereClause = "SELECT * FROM Document WHERE %s=\"%s\" AND (%s)";
        String queryNoWhereClause = "SELECT * FROM Document WHERE %s=\"%s\"";
        DocumentModelList result = new DocumentModelListImpl();
        for (String value : values) {
            String q = query != null ? String.format(queryWhereClause,
                    property, value, query) : String.format(queryNoWhereClause,
                    property, value);
            DocumentModelList docs = session.query(q);
            result.addAll(docs);
        }
        return result;
    }
}
