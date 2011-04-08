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
 *     Leroy Merlin (http://www.leroymerlin.fr/) - initial implementation
 * $Id$
 */
package org.eclipse.ecr.runtime.test.runner;

import static org.junit.Assert.assertNotNull;

import org.eclipse.ecr.test.framework.runner.Features;
import org.eclipse.ecr.test.framework.runner.FeaturesRunner;
import org.eclipse.ecr.test.framework.runner.Jetty;
import org.eclipse.ecr.test.framework.runner.JettyFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;

import com.google.inject.Inject;


@RunWith(FeaturesRunner.class)
@Features(JettyFeature.class)
@Jetty(port=9090)
public class JettyTest {

    @Inject
    Server server;

    @Test
    public void jettyComponentIsDeployed() {
        assertNotNull(server);
    }

}
