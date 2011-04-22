/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.eclipse.ecr.web.jaxrs;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * A jersey provider that expose by injection the current bundle to resources.
 * Resources can get the bundle owner by using the @Context annotation.
 * <p>
 * On newer OSGi frameworks the same can be done using {@link FrameworkUtil}.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Provider
public class BundleProvider implements InjectableProvider<Context, Type>, Injectable<Bundle> {

    protected Bundle bundle;

    public BundleProvider(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Bundle getValue() {
        return bundle;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext cc, Context a, Type t) {
        return t != Bundle.class ? null : this;
    }

}
