/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.eclipse.ecr.web.jaxrs.servlet.config;

import javax.servlet.ServletContextListener;

import org.eclipse.ecr.web.jaxrs.BundleNotFoundException;
import org.eclipse.ecr.web.jaxrs.Utils;
import org.eclipse.ecr.web.jaxrs.Utils.ClassRef;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("listener")
public class ListenerDescriptor {

    @XNode("@class")
    protected String classRef;

    private ClassRef ref;

    public ClassRef getClassRef() throws ClassNotFoundException, BundleNotFoundException {
        if (ref == null) {
            ref = Utils.getClassRef(classRef);
        }
        return ref;
    }

    public ServletContextListener getListener() throws Exception {
        return (ServletContextListener)getClassRef().get().newInstance();
    }

    @Override
    public String toString() {
        return classRef;
    }

}
