/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */
package org.eclipse.ecr.convert.impl;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.core.api.blobholder.SimpleBlobHolder;
import org.eclipse.ecr.core.api.impl.blob.StringBlob;
import org.eclipse.ecr.convert.api.ConversionException;
import org.eclipse.ecr.convert.api.ConversionService;
import org.eclipse.ecr.convert.extension.Converter;
import org.eclipse.ecr.convert.extension.ConverterDescriptor;
import org.eclipse.ecr.runtime.api.Framework;

/**
 * Converter that tries to find a way to extract full text content according to input mime-type.
 *
 * @author tiry
 */
public class FullTextConverter implements Converter {

    private static final String TEXT_PLAIN_MT = "text/plain";
    private static final Log log = LogFactory.getLog(FullTextConverter.class);

    protected ConverterDescriptor descriptor;

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {

        String srcMT;
        try {
            srcMT = blobHolder.getBlob().getMimeType();
        } catch (ClientException e) {
            throw new ConversionException("Unable to get source MimeType", e);
        }

        if (TEXT_PLAIN_MT.equals(srcMT)) {
            // no need to convert !
            return blobHolder;
        }

        ConversionService cs = Framework.getLocalService(ConversionService.class);

        String converterName = cs.getConverterName(srcMT, TEXT_PLAIN_MT);

        if (converterName != null) {
            if (converterName.equals(descriptor.getConverterName())) {
                // Should never happen !
                log.debug("Existing from converter to avoid a loop");
                return new SimpleBlobHolder(new StringBlob(""));
            }
            return cs.convert(converterName, blobHolder, parameters);
        } else {
            log.debug("Unable to find full text extractor for source mime type" + srcMT);
            return new SimpleBlobHolder(new StringBlob(""));
        }
    }

    @Override
    public void init(ConverterDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}
