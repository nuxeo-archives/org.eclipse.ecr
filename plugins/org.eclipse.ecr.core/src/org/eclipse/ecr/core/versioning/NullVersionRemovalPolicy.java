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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.model.Session;

/**
 * Version removal policy that does nothing.
 *
 * @author Florent Guillaume
 */
public class NullVersionRemovalPolicy implements VersionRemovalPolicy {

    private static final Log log = LogFactory.getLog(NullVersionRemovalPolicy.class);

    @Override
    public void removeVersions(Session session, Document doc,
            CoreSession coreSession) {
        log.debug("Removing no versions");
        // do nothing
    }

}
