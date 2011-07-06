/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.eclipse.ecr.runtime.login;

import java.security.Principal;

import org.eclipse.ecr.runtime.api.login.LoginComponent;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

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
