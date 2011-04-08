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
package org.eclipse.ecr.runtime.test.runner.web;

import junit.framework.Assert;

import org.eclipse.ecr.test.framework.runner.Features;
import org.eclipse.ecr.test.framework.runner.FeaturesRunner;
import org.eclipse.ecr.test.framework.runner.web.Browser;
import org.eclipse.ecr.test.framework.runner.web.BrowserFamily;
import org.eclipse.ecr.test.framework.runner.web.HomePage;
import org.eclipse.ecr.test.framework.runner.web.WebDriverFeature;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * To run such tests wu need such third parties that are not included by default
 */
@Ignore("this is making remote connections - it serves only for demonstrating webdriverfeature")
@RunWith(FeaturesRunner.class)
@Features(WebDriverFeature.class)
@HomePage(type=MyHomePage.class, url="http://www.google.com")
@Browser(type=BrowserFamily.HTML_UNIT)
public class WebTest {

    @Inject protected MyHomePage home;

    @Test public void testSearch() {
        SearchResultPage result = home.search("test");
        Assert.assertNotNull(result.getFirstResult());
        //        System.out.println(result.getFirstResult());
    }

}
