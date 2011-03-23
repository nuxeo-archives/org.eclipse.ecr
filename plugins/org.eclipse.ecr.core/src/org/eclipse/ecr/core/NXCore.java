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

package org.eclipse.ecr.core;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.ecr.core.lifecycle.LifeCycleService;
import org.eclipse.ecr.core.lifecycle.impl.LifeCycleServiceImpl;
import org.eclipse.ecr.core.model.NoSuchRepositoryException;
import org.eclipse.ecr.core.model.Repository;
import org.eclipse.ecr.core.repository.RepositoryService;
import org.eclipse.ecr.core.security.SecurityService;
import org.eclipse.ecr.runtime.api.Framework;

/**
 * CoreSession facade for services provided by NXCore module.
 * <p>
 * This is the main entry point to the core services.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author <a href="mailto:ja@nuxeo.com">Julien Anguenot</a>
 */
public final class NXCore {

    private NXCore() {
    }

    /**
     * Returns the life cycle service.
     *
     * @see LifeCycleServiceImpl
     *
     * @return the life cycle service
     */
    public static LifeCycleService getLifeCycleService() {
        return (LifeCycleService) Framework.getRuntime().getComponent(
                LifeCycleServiceImpl.NAME);
    }

    public static RepositoryService getRepositoryService() {
        return (RepositoryService) Framework.getRuntime().getComponent(
                RepositoryService.NAME);
    }

    public static Repository getRepository(String name)
            throws NoSuchRepositoryException {
        try {
//            needed by glassfish
//            return (Repository) new InitialContext()
//                .lookup("NXRepository/" + name);
            return (Repository) new InitialContext()
                    .lookup("java:NXRepository/" + name);
        } catch (NamingException e) {
            throw new NoSuchRepositoryException("Failed to lookup repository: "
                    + name, e);
        }
    }

    public static SecurityService getSecurityService() {
        return (SecurityService) Framework.getRuntime().getComponent(
                SecurityService.NAME);
    }

}
