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

package org.eclipse.ecr.core.query.sql.model;

import org.nuxeo.common.collections.SerializableArrayMap;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class FromList extends SerializableArrayMap<String, String> {

    private static final long serialVersionUID = -1931385427413643944L;

    @Override
    public String toString() {
        if (count == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();

        result.append(get(0));

        for (int i = 1; i < count; i++) {
            result.append(", ");
            result.append(get(i));
        }
        return result.toString();
    }

}
