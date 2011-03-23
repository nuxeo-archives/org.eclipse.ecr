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
package org.eclipse.ecr.automation.core.impl.adapters;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.automation.TypeAdaptException;
import org.eclipse.ecr.automation.TypeAdapter;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.DocumentRefList;
import org.eclipse.ecr.core.api.impl.DocumentRefListImpl;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocModelListToDocRefList implements TypeAdapter {

    public Object getAdaptedValue(OperationContext ctx, Object objectToAdapt)
            throws TypeAdaptException {
        DocumentModelList list = (DocumentModelList) objectToAdapt;
        DocumentRefList result = new DocumentRefListImpl(list.size());
        for (DocumentModel doc : list) {
            result.add(doc.getRef());
        }
        return result;
    }

}
