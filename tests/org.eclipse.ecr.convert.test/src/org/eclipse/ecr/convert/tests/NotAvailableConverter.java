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
 * $Id$
 */

package org.eclipse.ecr.convert.tests;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.convert.api.ConversionException;
import org.eclipse.ecr.convert.api.ConverterCheckResult;
import org.eclipse.ecr.convert.extension.ConverterDescriptor;
import org.eclipse.ecr.convert.extension.ExternalConverter;

public class NotAvailableConverter implements ExternalConverter {

    @Override
    public ConverterCheckResult isConverterAvailable() {
        return new ConverterCheckResult("Please install someting", "Can not find external converter");
    }

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {
        return null;
    }

    @Override
    public void init(ConverterDescriptor descriptor) {
    }

}
