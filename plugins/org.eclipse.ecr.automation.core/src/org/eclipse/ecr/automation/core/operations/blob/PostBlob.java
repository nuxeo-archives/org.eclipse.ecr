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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.ecr.automation.core.Constants;
import org.eclipse.ecr.automation.core.annotations.Operation;
import org.eclipse.ecr.automation.core.annotations.OperationMethod;
import org.eclipse.ecr.automation.core.annotations.Param;
import org.eclipse.ecr.automation.core.collectors.BlobCollector;
import org.eclipse.ecr.core.api.Blob;
import org.nuxeo.common.utils.FileUtils;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = PostBlob.ID, category = Constants.CAT_BLOB, label = "HTTP Post", description = "Post the input file to a target HTTP URL. Returns back the input file.")
public class PostBlob {

    public static final String ID = "Blob.Post";

    @Param(name = "url")
    protected String url;

    @OperationMethod(collector=BlobCollector.class)
    public Blob run(Blob blob) throws Exception {
        URL target = new URL(url);
        URLConnection conn = target.openConnection();
        conn.setDoOutput(true);
        InputStream in = blob.getStream();
        OutputStream out = conn.getOutputStream();
        try {
            FileUtils.copy(in, out);
            out.flush();
        } finally {
            in.close();
            out.close();
        }
        return blob;
    }

}
