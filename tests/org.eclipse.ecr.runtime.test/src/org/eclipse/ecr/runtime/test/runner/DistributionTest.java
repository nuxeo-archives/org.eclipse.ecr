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
 *     bstefanescu
 */
package org.eclipse.ecr.runtime.test.runner;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.test.framework.runner.Features;
import org.eclipse.ecr.test.framework.runner.FeaturesRunner;
import org.eclipse.ecr.test.framework.runner.distrib.DistributionFeature;
import org.eclipse.ecr.test.framework.runner.distrib.NuxeoDistribution;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Ignore("distribution dependency is causing class loader pb.")
@RunWith(FeaturesRunner.class)
@Features(DistributionFeature.class)
@NuxeoDistribution(profile="core-5.3.1-SNAPSHOT")
public class DistributionTest {

    @Test
    public void testAgainstDistribution() {
        System.out.println(Framework.getProperties());
    }

}
