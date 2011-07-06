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

import java.io.InputStream;
import java.net.URL;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.services.resource.ResourceService;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;
import org.nuxeo.common.utils.FileUtils;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ResourceServiceTest extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.eclipse.ecr.runtime.test", "resources/ResourcesContrib.xml");
    }

    public void testContributions() throws Exception {
        ResourceService rs = Framework.getLocalService(ResourceService.class);
        URL url = rs.getResource("myres");
        InputStream in = url.openStream();
        try {
            assertEquals("test resource", FileUtils.read(in));
        } finally {
            in.close();
        }
    }


}
