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
package org.eclipse.ecr.automation.core.collectors;

import java.util.ArrayList;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.automation.OperationException;
import org.eclipse.ecr.automation.OutputCollector;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.api.DocumentRefList;

/**
 * This implementation collect {@link DocumentRef} objects and return them as a
 * {@link DocumentRefList} object.
 * <p>
 * You may use this to automatically iterate over iterable inputs in operation
 * methods that <b>return</b> a {@link DocumentRef} object.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DocumentRefCollector extends ArrayList<DocumentRef> implements DocumentRefList,
        OutputCollector<DocumentRef, DocumentRefList> {

    private static final long serialVersionUID = 5732663048354570870L;

    @Override
    public long totalSize() {
        return size();
    }

    public void collect(OperationContext ctx, DocumentRef ref)
            throws OperationException {
        add(ref);
    }

    @Override
    public DocumentRefList getOutput() {
        return this;
    }
}
