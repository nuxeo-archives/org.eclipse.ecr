/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephane Lacoin
 */
package org.eclipse.ecr.runtime.test.runner;

import org.eclipse.ecr.runtime.RuntimeService;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.ComponentInstance;
import org.eclipse.ecr.runtime.model.RuntimeContext;
import org.eclipse.ecr.runtime.test.InlineRef;
import org.eclipse.ecr.testlib.protocols.inline.InlineURLsFeature;
import org.eclipse.ecr.testlib.runner.Features;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.RuntimeFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FeaturesRunner.class)
@Features({ RuntimeFeature.class, InlineURLsFeature.class })
public class CanExpandVariablesInContributionTest {

    RuntimeService runtime = Framework.getRuntime();

    @Before public void installDataHandler() {

    }

    @Before public void deployComponent() throws Exception {
        RuntimeContext ctx = runtime.getContext();
        System.setProperty("nuxeo.test.domain", "test");
        Framework.getProperties().setProperty("nuxeo.test.contrib", "contrib");
        InlineRef contribRef = new InlineRef("test", "<component name=\"${nuxeo.test.domain}:${nuxeo.test.contrib}\"/>");
        ctx.deploy(contribRef);
    }

    @Test public void variablesAreExpanded() throws Exception {
        ComponentInstance component = runtime.getComponentInstance("test:contrib");
        Assert.assertNotNull(component);
    }

}
