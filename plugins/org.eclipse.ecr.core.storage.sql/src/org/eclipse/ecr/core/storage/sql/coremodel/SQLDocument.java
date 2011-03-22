/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */

package org.eclipse.ecr.core.storage.sql.coremodel;

import org.eclipse.ecr.core.api.DocumentException;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.model.Property;
import org.eclipse.ecr.core.model.PropertyContainer;
import org.eclipse.ecr.core.storage.sql.Node;

/**
 * Base interface for SQL documents.
 */
public interface SQLDocument extends Document, PropertyContainer, Property {

    String BINARY_TEXT_SYS_PROP = "binaryText";
    String FULLTEXT_JOBID_SYS_PROP = "fulltextJobId";

    /**
     * Returns the node with info about the hierarchy location.
     */
    Node getNode();

    /**
     * Returns the property holding the ACL.
     */
    org.eclipse.ecr.core.model.Property getACLProperty() throws DocumentException;

    /**
     * Raises an exception if the document is read-only.
     */
    void checkWritable() throws DocumentException;

}
