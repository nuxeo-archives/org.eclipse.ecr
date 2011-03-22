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

package org.eclipse.ecr.core.api.local;

import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.CoreSessionFactory;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class LocalSessionFactory implements CoreSessionFactory {

    private static final long serialVersionUID = -5867927841500179645L;

    @Override
    public CoreSession getSession() {
        return new LocalSession();
    }

}
