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
package org.eclipse.ecr.convert.tests;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.convert.api.ConversionException;
import org.eclipse.ecr.convert.extension.Converter;
import org.eclipse.ecr.convert.extension.ConverterDescriptor;

public class DummyConverter implements Converter {

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {
        return null;
    }

    @Override
    public void init(ConverterDescriptor descriptor) {
    }

}
