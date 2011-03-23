/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.eclipse.ecr.opencmis.impl.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.repository.Repository;
import org.eclipse.ecr.core.api.repository.RepositoryManager;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.ComponentContext;
import org.eclipse.ecr.runtime.model.DefaultComponent;

/**
 * Information about all Nuxeo repositories.
 * <p>
 * Information is cached, and an initial connection to the repository is needed
 * to get the root folder id.
 */
public class NuxeoRepositories extends DefaultComponent {

    protected static Map<String, NuxeoRepository> repositories;

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        clear();
    }

    public static void clear() {
        repositories = null;
    }

    public static NuxeoRepository getRepository(String repositoryId) {
        if (repositories == null) {
            initRepositories();
        }
        return repositories.get(repositoryId);
    }

    public static List<NuxeoRepository> getRepositories() {
        if (repositories == null) {
            initRepositories();
        }
        return new ArrayList<NuxeoRepository>(repositories.values());
    }

    protected static void initRepositories() {
        repositories = Collections.synchronizedMap(new HashMap<String, NuxeoRepository>());
        try {
            RepositoryManager repositoryManager = Framework.getService(RepositoryManager.class);
            for (Repository repo : repositoryManager.getRepositories()) {
                String repositoryId = repo.getName();
                String rootFolderId;
                CoreSession coreSession = null;
                try {
                    coreSession = repositoryManager.getRepository(repositoryId).open();
                    rootFolderId = coreSession.getRootDocument().getId();
                } catch (ClientException e) {
                    throw new CmisRuntimeException(e.toString(), e);
                } finally {
                    if (coreSession != null) {
                        Repository.close(coreSession);
                    }
                }
                repositories.put(repositoryId, new NuxeoRepository(
                        repositoryId, rootFolderId));
            }
        } catch (CmisRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CmisRuntimeException(e.toString(), e);
        }
    }

}
