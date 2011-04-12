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

import java.util.Arrays;

import org.eclipse.ecr.core.event.EventService;
import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.FieldDescriptor;
import org.eclipse.ecr.core.storage.sql.testlib.DatabaseHelper;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author Florent Guillaume
 */
public abstract class SQLBackendTestCase extends NXRuntimeTestCase {

    public Repository repository;

    public Repository repository2;

    /** Set to false for client unit tests */
    public boolean initDatabase() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core");
        deployBundle("org.eclipse.ecr.core.schema");
        deployBundle("org.eclipse.ecr.core.event");
        deployBundle("org.eclipse.ecr.core.storage.sql");
        if (initDatabase()) {
            DatabaseHelper.DATABASE.setUp();
        }
        repository = newRepository(-1, false);
    }

    protected Repository newRepository(long clusteringDelay,
            boolean fulltextDisabled) throws Exception {
        RepositoryDescriptor descriptor = newDescriptor(clusteringDelay,
                fulltextDisabled);
        RepositoryImpl repo = new RepositoryImpl(descriptor);
        RepositoryResolver.registerTestRepository(repo);
        return repo;
    }

    protected RepositoryDescriptor newDescriptor(long clusteringDelay,
            boolean fulltextDisabled) {
        RepositoryDescriptor descriptor = DatabaseHelper.DATABASE.getRepositoryDescriptor();
        descriptor.clusteringEnabled = clusteringDelay != -1;
        descriptor.clusteringDelay = clusteringDelay;
        FieldDescriptor schemaField1 = new FieldDescriptor("tst:bignote",
                Model.FIELD_TYPE_LARGETEXT);
        FieldDescriptor schemaField2 = new FieldDescriptor("tst:bignotes",
                Model.FIELD_TYPE_LARGETEXT);
        descriptor.schemaFields = Arrays.asList(schemaField1, schemaField2);
        descriptor.binaryStorePath = "testbinaries";
        descriptor.fulltextDisabled = fulltextDisabled;
        return descriptor;
    }

    @Override
    public void tearDown() throws Exception {
        closeRepository();
        if (initDatabase()) {
            DatabaseHelper.DATABASE.tearDown();
        }
        super.tearDown();
    }

    protected void closeRepository() throws Exception {
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();
        if (repository != null) {
            repository.close();
            repository = null;
        }
        if (repository2 != null) {
            repository2.close();
            repository2 = null;
        }
    }

}
