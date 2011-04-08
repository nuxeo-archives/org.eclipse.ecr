/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Thomas Roger
 *     Florent Guillaume
 */

package org.eclipse.ecr.core.event.test;

import java.util.List;

import org.eclipse.ecr.core.event.Event;
import org.eclipse.ecr.core.event.EventService;
import org.eclipse.ecr.core.event.impl.EventContextImpl;
import org.eclipse.ecr.core.event.impl.EventImpl;
import org.eclipse.ecr.core.event.impl.EventListenerDescriptor;
import org.eclipse.ecr.core.event.impl.EventServiceImpl;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestEventServiceComponent extends NXRuntimeTestCase {

    public static final String BUNDLE = "org.eclipse.ecr.core.event";
    public static final String TEST_BUNDLE = "org.eclipse.ecr.core.event.test";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle(BUNDLE);
    }

    public void testDisablingListener() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/test-disabling-listeners1.xml");
        EventService service = Framework.getService(EventService.class);
        EventServiceImpl serviceImpl = (EventServiceImpl) service;

        List<EventListenerDescriptor> eventListenerDescriptors = serviceImpl.getEventListenerList().getSyncPostCommitListenersDescriptors();
        assertEquals(1, eventListenerDescriptors.size());

        EventListenerDescriptor eventListenerDescriptor = eventListenerDescriptors.get(0);
        assertTrue(eventListenerDescriptor.isEnabled());

        deployContrib(TEST_BUNDLE, "OSGI-INF/test-disabling-listeners2.xml");

        eventListenerDescriptors = serviceImpl.getEventListenerList().getSyncPostCommitListenersDescriptors();
        assertEquals(1, eventListenerDescriptors.size());

        eventListenerDescriptor = eventListenerDescriptors.get(0);
        assertFalse(eventListenerDescriptor.isEnabled());
    }

    /**
     * Test that when the event service component is deactivated, the threads
     * of the async event executor are shut down.
     */
    public void testAsyncEventExecutorShutdown() throws Exception {
        int initialCount = Thread.activeCount();
        // send an async event to make sure the async event executor spawned
        // some threads
        // load contrib
        deployContrib(TEST_BUNDLE, "OSGI-INF/test-PostCommitListeners3.xml");
        // send event
        EventService service = Framework.getService(EventService.class);
        Event event = new EventImpl("test1", new EventContextImpl());
        event.setIsCommitEvent(true);
        service.fireEvent(event);
        // wait for async processing to be done
        service.waitForAsyncCompletion();
        // check thread count increased
        assertTrue(Thread.activeCount() > initialCount);
        // now stop service
        // this is called by EventServiceComponent.deactivate() in real life
        ((EventServiceImpl) service).shutdown(2 * 1000);
        assertEquals(initialCount, Thread.activeCount());
    }

}
