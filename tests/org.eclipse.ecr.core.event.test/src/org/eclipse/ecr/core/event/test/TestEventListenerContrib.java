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

package org.eclipse.ecr.core.event.test;

import java.util.List;

import org.eclipse.ecr.core.event.EventService;
import org.eclipse.ecr.core.event.PostCommitEventListener;
import org.eclipse.ecr.core.event.impl.EventListenerDescriptor;
import org.eclipse.ecr.core.event.impl.EventServiceImpl;
import org.eclipse.ecr.core.event.script.ScriptingPostCommitEventListener;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class TestEventListenerContrib extends NXRuntimeTestCase {

    public static final String BUNDLE = "org.eclipse.ecr.core.event";

    public static final String TEST_BUNDLE = "org.eclipse.ecr.core.event.test";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle(BUNDLE);
    }

    public void testMerge() throws Exception {
        deployContrib(TEST_BUNDLE, "OSGI-INF/test-listeners.xml");

        EventService service = Framework.getService(EventService.class);
        EventServiceImpl serviceImpl = (EventServiceImpl) service;

        List<EventListenerDescriptor> inLineDescs = serviceImpl.getEventListenerList().getInlineListenersDescriptors();
        assertEquals(1, inLineDescs.size());
        assertEquals(1, serviceImpl.getEventListenerList().getInLineListeners().size());

        // check enable flag
        EventListenerDescriptor desc =  inLineDescs.get(0);
        desc.setEnabled(false);
        serviceImpl.addEventListener(desc);
        assertEquals(0, serviceImpl.getEventListenerList().getInLineListeners().size());

        desc.setEnabled(true);
        serviceImpl.addEventListener(desc);
        assertEquals(1, serviceImpl.getEventListenerList().getInLineListeners().size());

        // test PostCommit
        deployContrib(TEST_BUNDLE, "OSGI-INF/test-PostCommitListeners.xml");
        List<EventListenerDescriptor> apcDescs = serviceImpl.getEventListenerList().getAsyncPostCommitListenersDescriptors();
        assertEquals(1, apcDescs.size());
        assertEquals(1, serviceImpl.getEventListenerList().getAsyncPostCommitListeners().size());

        deployContrib(TEST_BUNDLE, "OSGI-INF/test-PostCommitListeners2.xml");
        assertEquals(0, serviceImpl.getEventListenerList().getAsyncPostCommitListeners().size());
        assertEquals(1, serviceImpl.getEventListenerList().getSyncPostCommitListeners().size());

        boolean isScriptListener = false;
        PostCommitEventListener listener = serviceImpl.getEventListenerList().getSyncPostCommitListeners().get(0);
        if ( listener instanceof ScriptingPostCommitEventListener) {
            isScriptListener=true;
        }
        assertTrue(isScriptListener);

        deployContrib(TEST_BUNDLE, "OSGI-INF/test-PostCommitListeners3.xml");
        assertEquals(1, serviceImpl.getEventListenerList().getAsyncPostCommitListeners().size());
        assertEquals(0, serviceImpl.getEventListenerList().getSyncPostCommitListeners().size());

        listener = serviceImpl.getEventListenerList().getAsyncPostCommitListeners().get(0);
        isScriptListener = listener instanceof ScriptingPostCommitEventListener;
        assertFalse(isScriptListener);
    }

}
