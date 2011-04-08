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
 *     Anahide Tchertchian
 */
package org.eclipse.ecr.runtime.login;

import java.security.Principal;

import org.eclipse.ecr.runtime.api.login.LoginComponent;
import org.eclipse.ecr.test.framework.NXRuntimeTestCase;

/**
 * @author Anahide Tchertchian
 */
public class TestLoginComponentSystemID extends NXRuntimeTestCase {

    public void testSystemIDEquals() {
        Principal user1 = new LoginComponent.SystemID(
                LoginComponent.SYSTEM_USERNAME);
        Principal user2 = new LoginComponent.SystemID(
                LoginComponent.SYSTEM_USERNAME);
        assertNotNull(user1);
        assertEquals(user1, user2);

        Principal otherUser = new LoginComponent.SystemID("toto");
        assertFalse(user1.equals(otherUser));

        Principal nullUser = new LoginComponent.SystemID();
        assertFalse(user1.equals(nullUser));
    }

}
