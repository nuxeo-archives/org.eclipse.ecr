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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DeploymentSet {

    protected final LinkedHashSet<String> deployments;
    protected final LinkedHashSet<String> localDeployments;

    public DeploymentSet() {
        deployments = new LinkedHashSet<String>();
        localDeployments = new LinkedHashSet<String>();
    }

    public void load(AnnotationScanner scanner, Class<?> clazz) {
        scanner.scan(clazz);
        List<? extends Annotation> annos = scanner.getAnnotations(clazz);
        if (annos == null) {
            return;
        }
        for (Annotation anno : annos) {
            if (anno.annotationType() == Deploy.class) {
                for (String key : ((Deploy) anno).value()) {
                    deployments.add(key);
                }
            } else if (anno.annotationType() == LocalDeploy.class) {
                for (String key : ((LocalDeploy) anno).value()) {
                    localDeployments.add(key);
                }
            }
        }
    }

    public void addDeployment(String key) {
        deployments.add(key);
    }

    public void addLocalDeployment(String key) {
        localDeployments.add(key);
    }

    public void addDeployment(Collection<String> key) {
        deployments.addAll(key);
    }

    public void addLocalDeployment(Collection<String> key) {
        localDeployments.addAll(key);
    }

    public Set<String> getDeployments() {
        return deployments;
    }

    public Set<String> getLocalDeployments() {
        return localDeployments;
    }

}
