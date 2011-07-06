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
package org.eclipse.ecr.testlib.runner.distrib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NuxeoDistribution {

    /**
     * The distribution name.
     * if config is not specified will try to locate a distribution configuration matching the profile.
     */
    String profile();

    /**
     * An URL that points to a custom distribution configuration.
     * Use "java:path_to_resource" to locate the configuration using the classloader
     */
    String config() default "";

    /**
     * The nuxeo server home.
     * Can use variables like {profile}, {tmp} for the temporary directory and ~ for the home directory.
     */
    String home() default "~/.nxserver/distrib/{profile}";

    String host() default "localhost";

    int port() default 8989;

    boolean useCache() default true;

    boolean offline() default false;

    String updatePolicy() default "daily";

}
