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
package org.eclipse.ecr.core.storage.sql.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.core.storage.Credentials;
import org.eclipse.ecr.core.storage.StorageException;
import org.eclipse.ecr.core.storage.sql.Mapper;
import org.eclipse.ecr.core.storage.sql.Model;
import org.eclipse.ecr.core.storage.sql.ModelSetup;
import org.eclipse.ecr.core.storage.sql.RepositoryBackend;
import org.eclipse.ecr.core.storage.sql.RepositoryImpl;
import org.eclipse.ecr.core.storage.sql.Session.PathResolver;

/**
 * Network client backend for a repository.
 */
public class NetBackend implements RepositoryBackend {

    private static final Log log = LogFactory.getLog(NetBackend.class);

    protected RepositoryImpl repository;

    @Override
    public void initialize(RepositoryImpl repository) throws StorageException {
        this.repository = repository;
    }

    @Override
    public void initializeModelSetup(ModelSetup modelSetup)
            throws StorageException {
        modelSetup.materializeFulltextSyntheticColumn = false; // TODO-H2
    }

    @Override
    public void initializeModel(Model model) throws StorageException {
    }

    @Override
    public Mapper newMapper(Model model, PathResolver pathResolver,
            Credentials credentials, boolean create) throws StorageException {
        try {
            return MapperClient.getMapper(repository, credentials);
        } catch (StorageException e) {
            String url = MapperClient.getUrl(repository.getRepositoryDescriptor());
            log.error("Failed to connect to server: " + url, e);
            throw e;
        }
    }

    @Override
    public void shutdown() {
    }

}
