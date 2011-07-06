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
 */
package org.eclipse.ecr.runtime.persistence;

import org.eclipse.ecr.runtime.DummyContribution;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.persistence.Contribution;
import org.eclipse.ecr.runtime.model.persistence.ContributionBuilder;
import org.eclipse.ecr.runtime.model.persistence.ContributionPersistenceManager;
import org.eclipse.ecr.runtime.services.event.Event;
import org.eclipse.ecr.runtime.services.event.EventService;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class TestContributionPersistence extends NXRuntimeTestCase {

    protected ContributionPersistenceManager mgr;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        fireFrameworkStarted();

        mgr = Framework.getLocalService(ContributionPersistenceManager.class);
    }

    public void test1() throws Exception {
        assertTrue(mgr.getContributions().isEmpty());
        // contribute something (an event listener)
        ContributionBuilder cb = new ContributionBuilder("contrib1");
        cb.setDescription("My first contribution");

        String xml = "<listener class=\"org.eclipse.ecr.runtime.persistence.MyListener\"><topic>test</topic></listener>";
        cb.addXmlExtension("org.eclipse.ecr.runtime.EventService", "listeners", xml);
        cb.addExtension("BaseXPoint", "xp", new DummyContribution("dummy1"),
                new DummyContribution("dummy2"));

        Contribution c1 = mgr.addContribution(cb);
        // check the created contribution
        String content = c1.getContent();
        assertTrue(content.contains("</component>"));
        // System.out.println(content);
        assertEquals("My first contribution", c1.getDescription());
        assertFalse(c1.isDisabled());

        assertEquals(1, mgr.getContributions().size());
        assertTrue(mgr.isPersisted(c1));
        assertFalse(mgr.isInstalled(c1));

        mgr.installContribution(c1);

        assertTrue(mgr.isPersisted(c1));
        assertTrue(mgr.isInstalled(c1));

        // fire an event.
        EventService es = Framework.getLocalService(EventService.class);
        assertEquals(0, MyListener.getCounter());
        es.sendEvent(new Event("test", "a test", null, null));
        assertEquals(1, MyListener.getCounter());
        es.sendEvent(new Event("test", "a test", null, null));
        assertEquals(2, MyListener.getCounter());

        mgr.uninstallContribution(c1);

        assertTrue(mgr.isPersisted(c1));
        assertFalse(mgr.isInstalled(c1));

        es.sendEvent(new Event("test", "a test", null, null));
        assertEquals(2, MyListener.getCounter());

        mgr.removeContribution(c1);
        assertEquals(0, mgr.getContributions().size());
        assertFalse(mgr.isPersisted(c1));
        assertFalse(mgr.isInstalled(c1));

    }
}
