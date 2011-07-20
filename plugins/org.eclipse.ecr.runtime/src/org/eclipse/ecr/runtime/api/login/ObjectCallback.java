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

package org.eclipse.ecr.runtime.api.login;

import javax.security.auth.callback.Callback;

/**
 * A simple callback handler that can be used to get authentication details
 * as a Java Object.
 * <p>
 * The object injected in that callback is specific to each CalbackHandler implementation.
 *
 * @author  eionica@nuxeo.com
 *
 */
public class ObjectCallback implements Callback {

    private Object obj;

    public ObjectCallback() {
    }

    public Object getObject() {
        return obj;
    }

    public void setObject(Object obj) {
        this.obj = obj;
    }
    
}
