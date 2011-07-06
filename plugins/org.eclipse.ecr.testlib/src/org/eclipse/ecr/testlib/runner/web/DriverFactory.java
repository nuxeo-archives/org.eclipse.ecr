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
package org.eclipse.ecr.testlib.runner.web;

import org.openqa.selenium.WebDriver;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface DriverFactory {

    /**
     * Gets the family of the driver this factory can create.
     */
    BrowserFamily getBrowserFamily();

    /**
     * Creates the driver.
     */
    WebDriver createDriver();

    /**
     * Disposes any needed resources after the driver was closed.
     */
    void disposeDriver(WebDriver driver);

}
