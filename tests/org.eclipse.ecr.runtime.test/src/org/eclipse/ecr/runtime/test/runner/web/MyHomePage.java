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

import org.eclipse.ecr.testlib.runner.web.WebPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class MyHomePage extends WebPage {

    @FindBy(how = How.NAME, using = "q")
    private WebElement search;

    public SearchResultPage search(String text) {
        search.clear();
        search.sendKeys(text);
        search.submit();
        return getPage(SearchResultPage.class);
    }

}
