/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
