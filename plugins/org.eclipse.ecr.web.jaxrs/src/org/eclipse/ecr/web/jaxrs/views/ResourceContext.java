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
package org.eclipse.ecr.web.jaxrs.views;

import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.web.jaxrs.ApplicationHost;
import org.eclipse.ecr.web.jaxrs.session.SessionFactory;
import org.eclipse.ecr.web.rendering.api.RenderingEngine;
import org.osgi.framework.Bundle;

/**
 * A resource request context.
 * This class is not thread safe.
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ResourceContext {

    private static ThreadLocal<ResourceContext> perThreadContext = new ThreadLocal<ResourceContext>();

    public final static void setContext(ResourceContext context) {
        perThreadContext.set(context);
    }

    public final static ResourceContext getContext() {
        return perThreadContext.get();
    }

    public final static void destroyContext() {
        perThreadContext.remove();
    }

    /**
     * The JAX-RS application providing the resources.
     */
    protected ApplicationHost app;

    protected HttpServletRequest request;

    protected UriInfo uriInfo;

    private LinkedList<Bundle> bundleStack;

    private CoreSession session;


    protected ResourceContext() {
    }

    public ResourceContext(ApplicationHost app) {
        //TODO rendering in app
        this.app = app;
        this.bundleStack = new LinkedList<Bundle>();
        //this.bundleStack.add(app.getBundle());
    }

    public ApplicationHost getApplication() {
        return app;
    }

    public final LinkedList<Bundle> getBundleStack() {
        return bundleStack;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public final Bundle getBundle() {
        return bundleStack.isEmpty() ? null : bundleStack.get(bundleStack.size()-1);
    }

    public final RenderingEngine getRenderingEngine() {
        return app.getRendering();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Principal getPrincipal() {
        return request.getUserPrincipal();
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }


    public CoreSession getSession() {
        if (session == null) {
            session = SessionFactory.getSession(request);
        }
        return session;
    }

    public URI getBaseUri() {
        return uriInfo.getBaseUri();
    }

    public void pushBundleFor(Object obj) {
        Bundle b = getResourceBundle(obj);
        if (b != null) {
            pushBundle(b);
        }
    }

    public void pushBundle(Bundle bundle) {
        for (Bundle b : bundleStack) {
            if (b == bundle) {
                // already present
                return;
            }
        }
        bundleStack.add(bundle);
    }

    protected Bundle getResourceBundle(Object res) {
        //return FrameworkUtil.getBundle(res.getClass());
        return app.getBundle(res.getClass());
    }


    /**
     * The prefix used to reference templates in template source locators
     * @return
     */
    public String getViewRoot() {
        return bundleStack.isEmpty() ? "" : "view:"+bundleStack.get(bundleStack.size()-1).getBundleId()+":/";
    }

    public URL findEntry(String path) {
        if (path.startsWith("view:")) {
            int p = path.indexOf(":/");
            if (p > -1) {
                path = path.substring(p+2);
            }
        }
        for (int i=bundleStack.size()-1; i>=0; i--) {
            URL url = bundleStack.get(i).getEntry(path);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

}
