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
 *     Stephane Lacoin
 */
package org.eclipse.ecr.runtime.test.runner;

import org.eclipse.ecr.runtime.RuntimeService;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.model.ComponentInstance;
import org.eclipse.ecr.runtime.model.RuntimeContext;
import org.eclipse.ecr.runtime.test.InlineRef;
import org.eclipse.ecr.test.framework.protocols.inline.InlineURLsFeature;
import org.eclipse.ecr.test.framework.runner.Features;
import org.eclipse.ecr.test.framework.runner.FeaturesRunner;
import org.eclipse.ecr.test.framework.runner.RuntimeFeature;
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
