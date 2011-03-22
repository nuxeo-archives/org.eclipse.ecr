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
package org.eclipse.ecr.automation.core;

import org.eclipse.ecr.automation.TypeAdapter;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@XObject("adapter")
public class TypeAdapterContribution {

    /**
     * Adapter implementation class
     */
    @XNode("@class")
    public Class<? extends TypeAdapter> clazz;

    @XNode("@accept")
    public Class<?> accept;

    @XNode("@produce")
    public Class<?> produce;

}
