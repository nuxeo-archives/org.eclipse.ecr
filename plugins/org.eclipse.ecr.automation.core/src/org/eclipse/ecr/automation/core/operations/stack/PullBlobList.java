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
@Operation(id = PullBlobList.ID, category = Constants.CAT_EXECUTION_STACK, label = "Pull File List", description = "Restore the first saved input file list in the context input stack")
public class PullBlobList {

    public static final String ID = "Blob.PullList";

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public BlobList run() throws Exception {
        Object obj = ctx.pull(Constants.O_BLOBS);
        if (obj instanceof BlobList) {
            return (BlobList) obj;
        }
        throw new OperationException(
                "Illegal state error for pull file list operation. The context stack doesn't contains a file list on its bottom");
    }

}
