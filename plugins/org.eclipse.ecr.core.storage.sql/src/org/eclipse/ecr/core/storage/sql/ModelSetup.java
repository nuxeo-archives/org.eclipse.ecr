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

package org.eclipse.ecr.core.storage.sql;

import org.eclipse.ecr.core.schema.SchemaManager;

/**
 * Info driving the model creation.
 */
public class ModelSetup {

    public RepositoryDescriptor repositoryDescriptor;

    public SchemaManager schemaManager;

    public boolean materializeFulltextSyntheticColumn;

}
