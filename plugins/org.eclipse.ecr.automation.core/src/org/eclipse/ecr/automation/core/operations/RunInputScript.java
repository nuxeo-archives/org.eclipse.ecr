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
package org.eclipse.ecr.automation.core.operations;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.automation.OperationException;
import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Context;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.automation.core.annotations.Param;
import org.eclipse.ecr.automation.core.scripting.Scripting.GroovyScript;
import org.eclipse.ecr.automation.core.scripting.Scripting.MvelScript;
import org.eclipse.ecr.core.api.Blob;
import org.eclipse.ecr.core.api.NuxeoPrincipal;
import org.eclipse.ecr.core.api.impl.blob.StringBlob;

/**
 * Run a script given as the input of the operation (as a blob).
 *
 * Note that this operation is available only as administrator
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = RunInputScript.ID, category = Constants.CAT_SCRIPTING, label = "Run Input Script", description = "Run a script from the input blob. A blob comtaining script result is returned.")
public class RunInputScript {

    public static final String ID = "Context.RunInputScript";

    @Context
    protected OperationContext ctx;

    @Param(name = "type", required = false, values = { "mvel", "groovy" }, widget = Constants.W_OPTION)
    protected String type = "mvel";

    @OperationMethod
    public Blob run(Blob blob) throws Exception {
        if (!((NuxeoPrincipal) ctx.getPrincipal()).isAdministrator()) {
            throw new OperationException(
                    "Not allowed. You must be administrator to run scripts");
        }
        Object r = null;
        if (type.equals("mvel")) {
            r = MvelScript.compile(blob.getString()).eval(ctx);
        } else if (type.equals("groovy")) {
            r = new GroovyScript(blob.getString()).eval(ctx);
        } else {
            throw new OperationException("Unknown scripting language " + type);
        }
        if (r != null) {
            StringBlob b = new StringBlob(r.toString());
            b.setMimeType("text/plain");
            b.setFilename("result");
            return b;
        }
        return null;
    }

}
