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

package org.eclipse.ecr.core.storage.sql.testlib;

import static org.eclipse.ecr.core.api.security.SecurityConstants.ADMINISTRATOR;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreInstance;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.NuxeoPrincipal;
import org.eclipse.ecr.core.event.EventService;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author Florent Guillaume
 */
public abstract class SQLRepositoryTestCase extends NXRuntimeTestCase {

    public static final String REPOSITORY_NAME = "test";

    public static final String BUNDLE = "org.eclipse.ecr.core.storage.sql.testlib";

    public CoreSession session;

    public DatabaseHelper database = DatabaseHelper.DATABASE;

    public SQLRepositoryTestCase() {
    }

    public SQLRepositoryTestCase(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.schema");
        deployBundle("org.eclipse.ecr.core.api");
        deployBundle("org.eclipse.ecr.core");
        deployBundle("org.eclipse.ecr.core.event");
        deployBundle("org.eclipse.ecr.core.storage.sql");
        database.setUp();
        deployRepositoryContrib();
    }

    protected void deployRepositoryContrib() throws Exception {
        deployContrib(BUNDLE, database.getDeploymentContrib());
    }

    @Override
    public void tearDown() throws Exception {
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();
        super.tearDown();
        database.tearDown();
    }

    public void openSession() throws ClientException {
        session = openSessionAs(ADMINISTRATOR);
        assertNotNull(session);
    }

    public CoreSession openSessionAs(String username) throws ClientException {
        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put("username", username);
        return CoreInstance.getInstance().open(REPOSITORY_NAME, context);
    }

    public CoreSession openSessionAs(NuxeoPrincipal principal)
    throws ClientException {
        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put("principal", principal);
        return CoreInstance.getInstance().open(REPOSITORY_NAME, context);
    }

    public void closeSession() {
        closeSession(session);
    }

    public void closeSession(CoreSession session) {
        CoreInstance.getInstance().close(session);
    }

}
