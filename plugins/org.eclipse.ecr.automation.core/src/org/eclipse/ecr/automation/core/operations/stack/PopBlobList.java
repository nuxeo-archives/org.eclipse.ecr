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
import org.eclipse.ecr.automation.core.util.BlobList;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = PopBlobList.ID, category = Constants.CAT_EXECUTION_STACK, label = "Pop File List", description = "Restore the last saved input file list in the context input stack")
public class PopBlobList {

    public static final String ID = "Blob.PopList";

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public BlobList run() throws Exception {
        Object obj = ctx.pop(Constants.O_BLOBS);
        if (obj instanceof BlobList) {
            return (BlobList) obj;
        }
        throw new OperationException(
                "Illegal state error for pop file list operation. The context stack doesn't contains a file list on its top");
    }

}
