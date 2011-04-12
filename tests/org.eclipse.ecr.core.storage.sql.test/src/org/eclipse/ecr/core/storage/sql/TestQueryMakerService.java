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

import java.util.List;

import org.eclipse.ecr.core.query.QueryFilter;
import org.eclipse.ecr.core.storage.StorageException;
import org.eclipse.ecr.core.storage.sql.Session.PathResolver;
import org.eclipse.ecr.core.storage.sql.jdbc.QueryMaker;
import org.eclipse.ecr.core.storage.sql.jdbc.QueryMakerDescriptor;
import org.eclipse.ecr.core.storage.sql.jdbc.QueryMakerService;
import org.eclipse.ecr.core.storage.sql.jdbc.SQLInfo;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestQueryMakerService extends NXRuntimeTestCase {

    public static final String BUNDLE = "org.eclipse.ecr.core.storage.sql";

    protected QueryMakerDescriptor desc;

    public static class DummyQueryMaker1 implements QueryMaker {
        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean accepts(String query) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Query buildQuery(SQLInfo sqlInfo, Model model,
                PathResolver pathResolver, String query,
                QueryFilter queryFilter, Object... params)
                throws StorageException {
            throw new UnsupportedOperationException();
        }
    }

    public static class DummyQueryMaker2 implements QueryMaker {
        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean accepts(String query) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Query buildQuery(SQLInfo sqlInfo, Model model,
                PathResolver pathResolver, String query,
                QueryFilter queryFilter, Object... params)
                throws StorageException {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle(BUNDLE); // will be implicit later
    }

    public void testBasic() throws Exception {
        QueryMakerService queryMakerService = Framework.getService(QueryMakerService.class);
        QueryMakerDescriptor d;
        List<Class<? extends QueryMaker>> l;

        int n = queryMakerService.getQueryMakers().size();

        // first
        d = new QueryMakerDescriptor();
        d.name = "A";
        d.queryMaker = DummyQueryMaker1.class;
        queryMakerService.registerQueryMaker(d);
        l = queryMakerService.getQueryMakers();
        assertEquals(n + 1, l.size());
        assertSame(DummyQueryMaker1.class, l.get(n));

        // second
        d = new QueryMakerDescriptor();
        d.name = "B";
        d.queryMaker = DummyQueryMaker2.class;
        queryMakerService.registerQueryMaker(d);
        l = queryMakerService.getQueryMakers();
        assertEquals(n + 2, l.size());
        assertSame(DummyQueryMaker1.class, l.get(n));
        assertSame(DummyQueryMaker2.class, l.get(n + 1));

        // disable first
        d = new QueryMakerDescriptor();
        d.name = "A";
        d.enabled = false;
        queryMakerService.registerQueryMaker(d);
        l = queryMakerService.getQueryMakers();
        assertEquals(n + 1, l.size());
        assertSame(DummyQueryMaker2.class, l.get(n));

        // override second
        d = new QueryMakerDescriptor();
        d.name = "B";
        d.queryMaker = DummyQueryMaker1.class;
        queryMakerService.registerQueryMaker(d);
        l = queryMakerService.getQueryMakers();
        assertEquals(n + 1, l.size());
        assertSame(DummyQueryMaker1.class, l.get(n));

        // add another of the first
        d = new QueryMakerDescriptor();
        d.name = "A";
        d.queryMaker = DummyQueryMaker2.class;
        queryMakerService.registerQueryMaker(d);
        l = queryMakerService.getQueryMakers();
        assertEquals(n + 2, l.size());
        assertSame(DummyQueryMaker1.class, l.get(n));
        assertSame(DummyQueryMaker2.class, l.get(n + 1));
    }

}
