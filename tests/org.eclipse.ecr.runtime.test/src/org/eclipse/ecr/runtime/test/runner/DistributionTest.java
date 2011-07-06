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
package org.eclipse.ecr.runtime.test.runner;

import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.runner.Features;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.distrib.DistributionFeature;
import org.eclipse.ecr.testlib.runner.distrib.NuxeoDistribution;
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
