/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Nuxeo - initial API and implementation
 */

package org.eclipse.ecr.core.api.localconfiguration;

import org.eclipse.ecr.core.api.DetachedAdapter;
import org.eclipse.ecr.core.api.DocumentRef;

/**
 * Interface that must be implemented by other interface representing a local
 * configuration.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4.2
 */
public interface LocalConfiguration<T> extends DetachedAdapter {

    /**
     * Returns the related {@code DocumentRef} of this local configuration.
     */
    DocumentRef getDocumentRef();

    /**
     * Returns {@code true} if this {@code LocalConfiguration} accepted to be
     * merged with a parent configuration, {@code false} otherwise.
     */
    boolean canMerge();

    /**
     * Merge this {@code LocalConfiguration} with another one.
     */
    T merge(T other);

}
