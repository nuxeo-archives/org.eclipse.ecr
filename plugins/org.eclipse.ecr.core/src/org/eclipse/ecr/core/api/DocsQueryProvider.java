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

package org.eclipse.ecr.core.api;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.model.Document;
import org.eclipse.ecr.core.model.DocumentIterator;

/**
 * @author <a href="mailto:dm@nuxeo.com">Dragos Mihalache</a>
 */
interface DocsQueryProvider {

    DocumentIterator getDocs(int start) throws ClientException;

    boolean accept(Document doc) throws ClientException;

}
