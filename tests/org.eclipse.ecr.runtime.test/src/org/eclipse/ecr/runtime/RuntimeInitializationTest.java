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

import junit.framework.AssertionFailedError;

import org.eclipse.ecr.test.framework.NXRuntimeTestCase;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class RuntimeInitializationTest extends NXRuntimeTestCase {

    public void testContributions() throws Exception {
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp1.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "resources/MyComp2.xml");
    }

    // Deactivated for now since duplicate contributions are still allowed.
    public void XXXtestContributionsWithDuplicateComponent() throws Exception {
        deployContrib("org.eclipse.ecr.runtime.test", "MyComp1.xml");
        deployContrib("org.eclipse.ecr.runtime.test", "MyComp2.xml");
        boolean success = false;
        try {
            deployContrib("org.eclipse.ecr.runtime.test", "CopyOfMyComp2.xml");
            success = true;
        } catch (AssertionFailedError e) {
            // OK.
        }
        assertFalse("An exception should have been raised.", success);
    }
}
