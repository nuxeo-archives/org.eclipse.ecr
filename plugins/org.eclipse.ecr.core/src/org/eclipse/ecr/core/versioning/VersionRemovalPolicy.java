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

package org.eclipse.ecr.core.versioning;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.model.Session;

/**
 * Interface for the policy that decides which versions have to be removed when
 * a working document is removed. This policy is called at the AbstractSession
 * level.
 *
 * @author Florent Guillaume
 */
public interface VersionRemovalPolicy {

    /**
     * Removes the versions when a given working document is about to be
     * removed.
     *
     * @param session the current session
     * @param doc the document that is about to be removed
     */
    void removeVersions(Session session, Document doc,
            CoreSession coreSession) throws ClientException;

}
