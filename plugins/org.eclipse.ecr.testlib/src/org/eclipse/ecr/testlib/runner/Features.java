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
package org.eclipse.ecr.testlib.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A feature is Nuxeo Runner extension that is able to configure the runner
 * from additional annotations and using a specific logic.
 * <p>
 * Let say you want a test that launches a Nuxeo Core with webengine and webdriver enabled.
 * You can activate these features using the Feature annotation like this:
 * <pre>
 * @RunWith(NuxeoRunner.class)
 * @Features({CoreFeature.class, WebDriverFeature.class, WebEngineFeature.class})
 * public class MyTest {
 *
 * }
 * </pre>
 *
 * or use the <code>@Features</code> annotation on an interface or subclass of your test class.
 * All the features presents on the class hierarchy will be collected and used.
 * <p>
 * Features must implement RunnerFeature class.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Features {

    Class<? extends RunnerFeature>[] value();

}
