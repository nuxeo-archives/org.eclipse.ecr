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
package org.eclipse.ecr.runtime.test.runner.web;

import junit.framework.Assert;

import org.eclipse.ecr.testlib.runner.Features;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.web.Browser;
import org.eclipse.ecr.testlib.runner.web.BrowserFamily;
import org.eclipse.ecr.testlib.runner.web.HomePage;
import org.eclipse.ecr.testlib.runner.web.WebDriverFeature;
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
