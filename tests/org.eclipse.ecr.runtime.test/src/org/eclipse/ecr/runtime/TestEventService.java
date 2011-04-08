/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.eclipse.ecr.runtime;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.services.event.Event;
import org.eclipse.ecr.runtime.services.event.EventService;
import org.eclipse.ecr.test.framework.NXRuntimeTestCase;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class TestEventService extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.eclipse.ecr.runtime.test", "resources/ListenerExtension.xml");
    }

    public void testSend() {
        EventService es = (EventService) Framework.getRuntime().getComponent(EventService.NAME);
        Event event = new Event("repository", "theId", this, null);
        es.sendEvent(event);
    }

}
