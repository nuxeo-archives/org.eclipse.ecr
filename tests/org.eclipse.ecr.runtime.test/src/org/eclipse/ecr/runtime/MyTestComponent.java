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

package org.eclipse.ecr.runtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.runtime.model.Component;
import org.eclipse.ecr.runtime.model.ComponentContext;
import org.eclipse.ecr.runtime.model.Extension;

import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class MyTestComponent implements Component {

    private static final Log log = LogFactory.getLog(MyTestComponent.class);

    @Override
    public void activate(ComponentContext context) {
        assertEquals("value", context.getProperty("myString").getValue());
        assertEquals(2, context.getProperty("myInt").getValue());
    }

    @Override
    public void deactivate(ComponentContext context) {
        // Auto-generated method stub
    }

    @Override
    public void registerExtension(Extension extension) {
        Object[] contribs = extension.getContributions();
        for (Object contrib : contribs) {
            log.debug("Registering: " + ((DummyContribution) contrib).message);
        }
    }

    @Override
    public void unregisterExtension(Extension extension) {
        Object[] contribs = extension.getContributions();
        for (Object contrib : contribs) {
            log.debug("Un-Registering: "
                    + ((DummyContribution) contrib).message);
        }
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
    }

}
