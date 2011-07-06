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

import org.eclipse.ecr.runtime.api.Framework;

import com.google.inject.Provider;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ServiceProvider<T> implements Provider<T> {

    protected final Class<?> clazz;

    public ServiceProvider(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T)Framework.getService(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service: "+clazz, e);
        }
    }

}
