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

import java.util.HashMap;

import javax.servlet.Filter;

import org.eclipse.ecr.web.jaxrs.BundleNotFoundException;
import org.eclipse.ecr.web.jaxrs.Utils;
import org.eclipse.ecr.web.jaxrs.Utils.ClassRef;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("filter")
public class FilterDescriptor {

    @XNode("@class")
    protected String classRef;

    @XNodeMap(value="property", key="@name", type=HashMap.class, componentType=String.class, trim=true, nullByDefault=false)
    protected HashMap<String, String> initParams;

    private ClassRef ref;

    public ClassRef getClassRef() throws ClassNotFoundException, BundleNotFoundException {
        if (ref == null) {
            ref = Utils.getClassRef(classRef);
        }
        return ref;
    }

    public String getRawClassRef() {
        return classRef;
    }

    public Filter getFilter() throws Exception {
        return (Filter)getClassRef().get().newInstance();
    }

    public HashMap<String, String> getInitParams() {
        return initParams;
    }

    @Override
    public String toString() {
        return classRef+" "+initParams;
    }
}
