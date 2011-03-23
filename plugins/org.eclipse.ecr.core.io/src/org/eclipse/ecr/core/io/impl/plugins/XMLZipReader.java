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
 *
 * $Id: XMLZipReader.java 29029 2008-01-14 18:38:14Z ldoguin $
 */

package org.eclipse.ecr.core.io.impl.plugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.eclipse.ecr.core.io.ExportConstants;
import org.eclipse.ecr.core.io.ExportedDocument;
import org.eclipse.ecr.core.io.impl.AbstractDocumentReader;
import org.eclipse.ecr.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.common.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class XMLZipReader extends AbstractDocumentReader {

    private ZipFile zip;

    public XMLZipReader(ZipFile zip) {
        this.zip = zip;
    }

    public XMLZipReader(String source) throws IOException {
        this(new ZipFile(source));
    }

    public XMLZipReader(File source) throws IOException {
        this(new ZipFile(source));
    }

    @Override
    // the zip entry order is the same as one used when creating the zip
    public ExportedDocument read() throws IOException {
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                return createDocument(entry);
            }
        }
        return null;
    }

    @Override
    public void close() {
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // do nothing
            } finally {
                zip = null;
            }
        }
    }

    private ExportedDocument createDocument(ZipEntry dirEntry)
            throws IOException {
        ExportedDocument xdoc = new ExportedDocumentImpl();
        String dirPath = dirEntry.getName();
        // TODO -> some processing on the path?
        xdoc.setPath(new Path(dirPath).removeTrailingSeparator());
        // read the main document
        ZipEntry entry = zip.getEntry(dirPath + ExportConstants.DOCUMENT_FILE);
        InputStream in = zip.getInputStream(entry);
        try {
            Document doc = readXML(in);
            doc.setDocument(doc);
        } finally {
            in.close();
        }

        return null;
    }

    public Document readXML(InputStream in) {
        return null;
    }

}
