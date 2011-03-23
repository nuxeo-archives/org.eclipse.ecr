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
 *     Florent Guillaume
 */

package org.eclipse.ecr.core.security;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.core.api.Lock;
import org.eclipse.ecr.core.api.security.ACP;
import org.eclipse.ecr.core.api.security.Access;
import org.eclipse.ecr.core.api.security.SecurityConstants;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.query.sql.model.SQLQuery;

/**
 * Security policy that blocks WRITE permission on a document if it is locked by
 * someone else.
 *
 * @author Anahide Tchertchian
 * @author Florent Guillaume
 */
public class LockSecurityPolicy extends AbstractSecurityPolicy {

    private static final Log log = LogFactory.getLog(LockSecurityPolicy.class);

    @Override
    public Access checkPermission(Document doc, ACP mergedAcp,
            Principal principal, String permission,
            String[] resolvedPermissions, String[] additionalPrincipals) {
        Access access = Access.UNKNOWN;
        // policy only applies on WRITE
        if (resolvedPermissions == null
                || !Arrays.asList(resolvedPermissions).contains(
                        SecurityConstants.WRITE)) {
            return access;
        }
        // check the lock
        try {
            String username = principal.getName();
            Lock lock = doc.getLock();
            if (lock != null && !username.equals(lock.getOwner())) {
                // locked by another user => deny
                access = Access.DENY;
            }
        } catch (Exception e) {
            // ignore
            log.debug("Failed to get lock status on document ", e);
        }
        return access;
    }

    @Override
    public boolean isRestrictingPermission(String permission) {
        assert permission.equals("Browse"); // others not coded
        return false;
    }

    @Override
    public boolean isExpressibleInQuery() {
        return true;
    }

    @Override
    public SQLQuery.Transformer getQueryTransformer() {
        return SQLQuery.Transformer.IDENTITY;
    }

}
