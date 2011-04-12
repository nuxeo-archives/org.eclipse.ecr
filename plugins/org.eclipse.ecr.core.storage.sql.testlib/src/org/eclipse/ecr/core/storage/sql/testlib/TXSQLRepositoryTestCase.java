/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume, jcarsique
 */

package org.eclipse.ecr.core.storage.sql.testlib;

import org.nuxeo.common.jndi.NamingContextFactory;
import org.eclipse.ecr.runtime.jtajca.NuxeoContainer;
import org.eclipse.ecr.runtime.transaction.TransactionHelper;

/**
 * Transactional SQL Repository TestCase.
 * <p>
 * The tests are run with a session that is open in a transaction.
 */
public class TXSQLRepositoryTestCase extends SQLRepositoryTestCase {

    public static final String BUNDLE = "org.eclipse.ecr.core.storage.sql.testlib";

    @Override
    public void setUp() throws Exception {
        NamingContextFactory.setAsInitial();
        setUpContainer();
        super.setUp(); // calls deployRepositoryConfig()
        TransactionHelper.startTransaction();
        openSession();
    }

    /** Can be subclassed to instantiate specific pool config. */
    protected void setUpContainer() throws Exception {
        NuxeoContainer.install();
    }

    /**
     * Overridden to use a pooling configuration.
     */
    @Override
    protected void deployRepositoryContrib() throws Exception {
        if (database instanceof DatabaseH2) {
            String contrib = "OSGI-INF/test-pooling-h2-contrib.xml";
            deployContrib(BUNDLE, contrib);
        } else if (database instanceof DatabasePostgreSQL) {
            String contrib = "OSGI-INF/test-pooling-postgres-contrib.xml";
            deployContrib(BUNDLE, contrib);
        } else {
            super.deployRepositoryContrib();
        }
    }

    protected boolean hasPoolingConfig() {
        return database instanceof DatabaseH2
                || database instanceof DatabasePostgreSQL;
    }

    @Override
    public void tearDown() throws Exception {
        session.cancel();
        if (TransactionHelper.isTransactionActiveOrMarkedRollback()) {
            TransactionHelper.setTransactionRollbackOnly();
            TransactionHelper.commitOrRollbackTransaction();
        }
        closeSession();
        super.tearDown();
        NamingContextFactory.revertSetAsInitial();
    }

}
