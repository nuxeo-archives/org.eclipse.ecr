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
package org.eclipse.ecr.core.event.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.rmi.dgc.VMID;

import org.eclipse.ecr.core.event.Event;
import org.eclipse.ecr.core.event.EventService;
import org.eclipse.ecr.core.event.impl.EventContextImpl;
import org.eclipse.ecr.core.event.impl.EventImpl;
import org.eclipse.ecr.core.event.impl.EventServiceImpl;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.RuntimeContext;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;
import org.junit.Ignore;

/**
 * TODO add tests on post commit.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class EventListenerTest extends NXRuntimeTestCase {

    public static final String BUNDLE = "org.eclipse.ecr.core.event";

    public static final String TEST_BUNDLE = "org.eclipse.ecr.core.event.test";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle(BUNDLE);
    }

    public void testFlags() {
        EventImpl event = new EventImpl("test", null);
        assertTrue(event.isPublic());
        assertEquals(Event.FLAG_NONE, event.getFlags());

        event.setLocal(true);
        event.setInline(true);
        assertEquals(Event.FLAG_LOCAL | Event.FLAG_INLINE, event.getFlags());
        assertTrue(event.isInline());
        assertTrue(event.isLocal());

        event.setLocal(false);
        assertEquals(Event.FLAG_INLINE, event.getFlags());
        assertTrue(event.isInline());
        assertFalse(event.isLocal());

        event.setInline(false);
        assertEquals(Event.FLAG_NONE, event.getFlags());
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());

        event.setPublic(false);
        assertFalse(event.isPublic());
        assertTrue(event.isLocal());
        assertFalse(event.isInline());
        assertFalse(event.isCommitEvent());

        event.setPublic(true);
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());
        assertFalse(event.isInline());
        assertFalse(event.isCommitEvent());

        event.setInline(true);
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());
        assertTrue(event.isInline());
        assertFalse(event.isCommitEvent());

        event.setIsCommitEvent(true);
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());
        assertTrue(event.isInline());
        assertTrue(event.isCommitEvent());

        event.setIsCommitEvent(false);
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());
        assertTrue(event.isInline());
        assertFalse(event.isCommitEvent());

        event.setInline(false);
        assertTrue(event.isPublic());
        assertFalse(event.isLocal());
        assertFalse(event.isInline());
        assertFalse(event.isCommitEvent());

        event.setPublic(false);
        assertFalse(event.isPublic());
        assertTrue(event.isLocal());
        assertFalse(event.isInline());
        assertFalse(event.isCommitEvent());
    }

    public void testEventCreation() {
        EventContextImpl ctx = new EventContextImpl();
        Event event = ctx.newEvent("test");
        assertEquals("test", event.getName());
        assertEquals(ctx, event.getContext());
        assertEquals(Event.FLAG_NONE, event.getFlags());

        event = ctx.newEvent("test2", Event.FLAG_COMMIT | Event.FLAG_INLINE);
        assertEquals("test2", event.getName());
        assertEquals(ctx, event.getContext());
        assertEquals(Event.FLAG_COMMIT | Event.FLAG_INLINE, event.getFlags());
    }

    public void testTimestamp() {
        long tm = System.currentTimeMillis();
        EventContextImpl ctx = new EventContextImpl();
        Event event = ctx.newEvent("test");
        assertTrue(tm <= event.getTime());
    }

    /**
     * The script listener will update this counter
     */
    public static int SCRIPT_CNT = 0;

    // TODO include groovy-engine
    @Ignore
    public void TODOtestScripts() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/test-listeners.xml");
        assertEquals(0, SCRIPT_CNT);

        EventService service = Framework.getService(EventService.class);
        service.fireEvent("test", new EventContextImpl(null, null));
        assertEquals(1, SCRIPT_CNT);

        undeployContrib(TEST_BUNDLE, "OSGI-INF/test-listeners.xml");
        assertEquals(1, SCRIPT_CNT);

        service.fireEvent("test", new EventContextImpl(null, null));
        assertEquals(1, SCRIPT_CNT);

        deployContrib(TEST_BUNDLE, "OSGI-INF/test-listeners.xml");
        service.fireEvent("test1", new EventContextImpl(null, null));
        assertEquals(2, SCRIPT_CNT);

        // test not accepted event
        service.fireEvent("some-event", new EventContextImpl(null, null));
        assertEquals(2, SCRIPT_CNT);
    }


    public void testRemoteForwarding() throws Exception {
        VMID vmid1 = EventServiceImpl.VMID; // the source vmid
        // generate another different vmid that will be used as the target host VMID
        VMID vmid2 = new VMID();
        int cnt = 0;
        while (vmid2.equals(vmid1)) {
            Thread.sleep(1000);
            vmid2 = new VMID();
            if (cnt++ >10) {
                fail("Unable to complete test - unable to generate a target vmid");
            }
        }

        FakeEventBundle event = new FakeEventBundle();
        assertFalse(event.hasRemoteSource());

        // change the vmid of the event as it was created on another machine
        event.setVMID(vmid2);
        assertTrue(event.hasRemoteSource());

        // serialize the event as it was sent from vmid2 to vmid1
        event = (FakeEventBundle) serialize(event);
        // now test the event - it should be marked as remote
        assertTrue(event.hasRemoteSource());
        // redo the test but with a non remote event

        event = new FakeEventBundle();
        assertFalse(event.hasRemoteSource());

        event = (FakeEventBundle) serialize(event);
        // now test the event - it should be marked as local
        assertFalse(event.hasRemoteSource());
    }

    public static Object serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        out.flush();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        return in.readObject();
    }

}
