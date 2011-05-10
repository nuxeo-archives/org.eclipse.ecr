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
package org.eclipse.ecr.auth;

import org.eclipse.ecr.runtime.api.login.Authenticator;
import org.eclipse.ecr.runtime.model.ComponentContext;
import org.eclipse.ecr.runtime.model.ComponentInstance;
import org.eclipse.ecr.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DefaultUserManagerComponent extends DefaultComponent {

    public final static String USERS_XP = "users";

    protected SimpleUserRegistry registry;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter == Authenticator.class || adapter == SimpleUserRegistry.class) {
            return (T)registry;
        }
        return null;
    }

    @Override
    public void activate(ComponentContext context) throws Exception {
        registry = new SimpleUserRegistry();
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        registry = null;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
    throws Exception {
        if (USERS_XP.equals(extensionPoint)) {
            registry.add((SimpleNuxeoPrincipal)contribution);
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
    throws Exception {
        if (USERS_XP.equals(extensionPoint)) {
            registry.remove((SimpleNuxeoPrincipal)contribution);
        }
    }

}
