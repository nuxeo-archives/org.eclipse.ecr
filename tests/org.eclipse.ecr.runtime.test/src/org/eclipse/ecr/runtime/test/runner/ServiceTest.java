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

import junit.framework.Assert;

import org.eclipse.ecr.runtime.services.event.EventService;
import org.eclipse.ecr.testlib.runner.Features;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.RuntimeFeature;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@RunWith(FeaturesRunner.class)
@Features(RuntimeFeature.class)
public class ServiceTest {
    @Inject EventService eventService;
    @Test public void testService() {
        Assert.assertNotNull(eventService);
    }
}
