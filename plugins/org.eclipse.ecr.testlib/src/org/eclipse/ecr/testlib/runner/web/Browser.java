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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Browser configuration
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Browser {

    /**
     * The type of the browser to use. Ignored if a factory is also specified.
     * When the type is specified a default driver for that type will be automatically created.
     */
    BrowserFamily type() default BrowserFamily.HTML_UNIT;

    /**
     * A custom factory to create WebDriver objects.
     * If a factory is specified the {@link #type()} is ignored.
     * Factories are good for customizing the driver creation in QA tests.
     */
    Class<? extends DriverFactory> factory() default DriverFactory.class;

}
