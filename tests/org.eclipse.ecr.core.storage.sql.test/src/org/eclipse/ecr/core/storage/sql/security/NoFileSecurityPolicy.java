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
package org.eclipse.ecr.core.storage.sql.security;

import java.security.Principal;

import org.eclipse.ecr.core.api.security.ACP;
import org.eclipse.ecr.core.api.security.Access;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.query.sql.model.SQLQuery.Transformer;
import org.eclipse.ecr.core.security.AbstractSecurityPolicy;
import org.eclipse.ecr.core.security.SecurityPolicy;

/**
 * Dummy security policy denying all access to File objects.
 *
 * @author Florent Guillaume
 */
public class NoFileSecurityPolicy extends AbstractSecurityPolicy implements
        SecurityPolicy {

    @Override
    public Access checkPermission(Document doc, ACP mergedAcp,
            Principal principal, String permission,
            String[] resolvedPermissions, String[] additionalPrincipals) {
        if (doc.getType().getName().equals("File")) {
            return Access.DENY;
        }
        return Access.UNKNOWN;
    }

    @Override
    public boolean isRestrictingPermission(String permission) {
        return true;
    }

    @Override
    public boolean isExpressibleInQuery() {
        return false;
    }

    @Override
    public Transformer getQueryTransformer() {
        throw new UnsupportedOperationException();
    }

}
